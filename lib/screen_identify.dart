import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:truface/settings/screen_settings.dart';
import 'package:truface/storage_util.dart';
import 'package:truface/trn_enroll.dart';

import 'photo_util.dart';
import 'trueface_util.dart';
import 'enroll/screen_enroll_list.dart';

class ScreenIdentify extends StatefulWidget {
  @override
  _ScreenIdentifyState createState() => _ScreenIdentifyState();
}

class _ScreenIdentifyState extends State<ScreenIdentify> {
  bool _loading = false;
  String _truefaceResult;
  AnimationController ac;

  MethodChannel _myChannel = MethodChannel('flutter.mc.channel');

  File activeSelfie;

  @override
  void initState() {
    super.initState();

    // this is buggy. please use common await invokeMethod !
    // _myChannel.setMethodCallHandler((call) async {
    //   switch (call.method) {
    //     case 'truface.camera.live.result':
    //       dynamic ss = jsonDecode(call.arguments);

    //       if (ss != null) {
    //         TrnEnroll _e = TrnEnroll.fromJson(ss);

    //         setState(() {
    //           _truefaceResult = 'Hello, ${_e.groupName}'.toUpperCase();

    //           activeSelfie = new File(_e.fileName);
    //         });
    //       }
    //   }
    //   return new Future.value("");
    // });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text('Trueface Demo'),
        actions: [
          ElevatedButton.icon(
            icon: Text('Enroll'),
            onPressed: () {
              ScreenEnrollList.go(context);
            },
            label: Icon(Icons.add),
          ),
          IconButton(
              icon: Icon(Icons.settings),
              onPressed: () {
                if (this.mounted)
                  setState(() {
                    _loading = false;
                    activeSelfie = null;
                  });

                ScreenSettings.go(context);
              })
        ],
      ),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            InkWell(
              child: CircleAvatar(
                radius: 100,
                backgroundImage: activeSelfie == null
                    ? AssetImage('images/avatar_dummy1.png')
                    : FileImage(activeSelfie),
              ),
              onTap: () async {
                File file = await PhotoUtil.takePhotoGeneric();

                if (file == null) return;

                if (this.mounted)
                  setState(() {
                    activeSelfie = file;
                  });
              },
            ),
            Divider(),
            _truefaceResult == null
                ? SizedBox()
                : Text(
                    _truefaceResult,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
            SizedBox(
              height: 10,
            ),
            _loading
                ? CircularProgressIndicator()
                : ElevatedButton.icon(
                    label: Padding(
                      padding: const EdgeInsets.all(18.0),
                      child: Text(
                        'SCAN',
                        style: TextStyle(letterSpacing: 8),
                      ),
                    ),
                    onPressed: () async {
                      if (activeSelfie == null) return;

                      setState(() {
                        _loading = true;
                        _truefaceResult = null;
                      });

                      // await Future.delayed(Duration(seconds: 1));

                      try {
                        String ret = await TruefaceUtil.scanPhoto(activeSelfie);

                        if (ret != null) {
                          _truefaceResult = ret.toUpperCase();
                        }
                      } finally {
                        if (this.mounted)
                          setState(() {
                            _loading = false;
                          });
                      }
                    },
                    icon: Icon(Icons.person_search_outlined),
                  ),
            SizedBox(
              height: 20,
            ),
            _loading
                ? CircularProgressIndicator()
                : ElevatedButton.icon(
                    label: Padding(
                      padding: const EdgeInsets.all(18.0),
                      child: Text(
                        'LIVE FACE',
                        style: TextStyle(letterSpacing: 8),
                      ),
                    ),
                    onPressed: () async {
                      setState(() {
                        _loading = true;
                        _truefaceResult = null;
                      });

                      // await Future.delayed(Duration(seconds: 1));

                      try {
                        double scoreSimilarity =
                            await StorageUtil.getScoreSimilarity();
                        String ret = await TruefaceUtil.livePhoto(
                            _myChannel, scoreSimilarity);

                        if (ret != null) {
                          _truefaceResult = ret.toUpperCase();
                        }
                      } finally {
                        if (this.mounted)
                          setState(() {
                            _loading = false;
                          });
                      }
                    },
                    icon: Icon(Icons.person_search_outlined),
                  ),
          ],
        ),
      ),
    );
  }
}
