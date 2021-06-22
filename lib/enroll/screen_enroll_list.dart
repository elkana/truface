import 'dart:io';

import 'package:flutter/material.dart';
import 'package:truface/enroll/screen_add_group.dart';

import '../trueface_util.dart';
import '../trn_enroll.dart';

class ScreenEnrollList extends StatefulWidget {
  @override
  _ScreenEnrollState createState() => _ScreenEnrollState();

  static void go(BuildContext context) {
    Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => ScreenEnrollList(
                // contractNo: contractNo,
                )));
  }
}

class _ScreenEnrollState extends State<ScreenEnrollList> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('People'),
        actions: [
          ElevatedButton.icon(
            onPressed: () {
              ScreenAddGroup.go(context, (val) {
                if (val && this.mounted) setState(() {});
              });
              // bool ret = await TruefaceUtil.enroll('elkana');

              // if (ret) setState(() {});
            },
            icon: Text('Add Person'),
            label: Icon(Icons.add),
          ),
          IconButton(
              icon: Icon(Icons.clear_all_outlined),
              onPressed: () async {
                await TruefaceUtil.clearAll();

                if (this.mounted) setState(() {});
              }),
        ],
      ),
      body: FutureBuilder<List<TrnEnroll>>(
          future: TruefaceUtil.getAllEnroll(),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting)
              return CircularProgressIndicator();

            if (!snapshot.hasData) return Text('No Data. Add Enroll first.');

            return ListView.separated(
              separatorBuilder: (context, index) => SizedBox(),
              itemCount: snapshot.data.length,
              itemBuilder: (context, index) => Dismissible(
                key: Key(snapshot.data[index].uid),
                child: Padding(
                    padding: EdgeInsets.all(8.0),
                    child: Card(
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: ListTile(
                          title: Text(
                              snapshot.data[index].groupName.toUpperCase()),
                          leading: CircleAvatar(
                            radius: 30,
                            backgroundImage: FileImage(
                                new File(snapshot.data[index].fileName)),
                          ),
                          // leading: SizedBox(
                          //   width: 80,
                          //   height: 100,
                          //   child:
                          //       Image.file(new File(snapshot.data[index].fileName)),
                          // ),
                        ),
                      ),
                    )
                    // Center(child: Text("Index $index")),
                    ),
                onDismissed: (direction) async {
                  bool ret =
                      await TruefaceUtil.delete(snapshot.data[index].uid);

                  if (ret && this.mounted) setState(() {});
                },
              ),
            );
          }),
    );
  }
}
