import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:truface/photo_util.dart';
import 'package:truface/screen_identify.dart';
import 'package:truface/trn_enroll.dart';

import 'wgallery.dart';

void main() {

  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Trueface Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.deepPurple,
        // This makes the visual density adapt to the platform that you run
        // the app on. For desktop platforms, the controls will be smaller and
        // closer together (more dense) than on mobile platforms.
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: ScreenIdentify(),
      // home: MyHomePage(title: 'TrueFace Demo'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  List<String> thumbnails;

  static const platform = const MethodChannel("flutter.mc.channel");

  @override
  void initState() {
    super.initState();

    platform.setMethodCallHandler((call) async {
      //
      // switch (call.method) {
      //   case ""
      // }
      print('call.method = ${call.method}, arguments = ${call?.arguments}');
    });
  }

  void _incrementCounter() {
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
        actions: [
          IconButton(
              icon: Icon(Icons.add),
              onPressed: () {
                //
              })
        ],
      ),
      body: SingleChildScrollView(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
            Text('SETUP'),
            ElevatedButton(
              child:
                  Text('Native: Truface Check License\ntruface.check.license'),
              onPressed: () {
                platform.invokeMethod("truface.check.license");
              },
            ),
            ElevatedButton(
              child: Text('Native: Truface Clear'),
              onPressed: () {
                platform.invokeMethod("truface.clear");
              },
            ),
            ElevatedButton(
              child: Text('Native: Truface GROUP Face\ntruface.group.face'),
              onPressed: () async {
                dynamic result =
                    await platform.invokeMethod("truface.group.face", {
                  'group': 'elkana',
                });

                print('Flutter terima $result');

                thumbnails = [];

                List<dynamic> ss = jsonDecode(result);
                for (int i = 0; i < ss.length; i++) {
                  TrnEnroll value = TrnEnroll.fromJson(ss[i]);

                  thumbnails.add(value.fileName);
                }
                print('Ada Thumbnails ${thumbnails?.length ?? -1}');

                setState(() {});
              },
            ),
            Text('ENROLLMENT'),
            ElevatedButton(
              child: Text(
                  'Flutter: Enroll using Camera Picker\ntruface.enroll.face'),
              onPressed: () async {
                File file = await PhotoUtil.takePhotoGeneric();

                dynamic ret =
                    await platform.invokeMethod('truface.enroll.face', {
                  'file': file.path,
                  'group': 'elkana',
                });

                print('Flutter: Enroll using Camera Picker -> $ret');
              },
            ),
            Divider(),
            Text('RECOGNITION'),
            ElevatedButton(
              child: Text('Flutter: Recognition\ntruface.check.face'),
              onPressed: () async {
                File file = await PhotoUtil.takePhotoGeneric();

                dynamic ret =
                    await platform.invokeMethod('truface.check.face', {
                  'file': file.path,
                });

                print('Flutter: Recognition -> $ret');
              },
            ),
            Divider(),
            Text('BASIC'),
            ElevatedButton(
              child: Text(
                  'Flutter Test: Photo using Camera Picker\nnative.receiveFileFromFlutter'),
              onPressed: () async {
                File file = await PhotoUtil.takePhotoGeneric();

                dynamic ret = await platform
                    .invokeMethod('native.receiveFileFromFlutter', {
                  'file': file.path,
                });

                print('Flutter: Enroll using Camera Picker -> $ret');
              },
            ),
            ElevatedButton(
              child: Text('Native: Camera\nnative.camera'),
              onPressed: () {
                platform.invokeMethod("native.camera");
              },
            ),
            ElevatedButton(
              child:
                  Text('Flutter: Camera Picker\nnative.receiveFileFromFlutter'),
              onPressed: () async {
                File file = await PhotoUtil.takePhotoGeneric();

                dynamic ret =
                    platform.invokeMethod('native.receiveFileFromFlutter', {
                  'file': file.path,
                });

                print('Flutter: Enroll using Camera Picker -> $ret');
              },
            ),
            ElevatedButton(
              child: Text(
                  'Flutter: Call Image Picker\nnative.receiveFileFromFlutter'),
              onPressed: () async {
                File file = await PhotoUtil.takePhotoFromGallery();

                dynamic ret =
                    platform.invokeMethod('native.receiveFileFromFlutter', {
                  'file': file.path,
                });

                print('Flutter: Enroll using Image Picker -> $ret');
              },
            ),
            MyGallery(files: thumbnails),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
