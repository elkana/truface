import 'dart:io';

import 'package:flutter/material.dart';

class MyGallery extends StatelessWidget {
  final List<String> files;

  const MyGallery({Key key, this.files}) : super(key: key);

  Future<List<Widget>> buildThumbnails() async {
    List<Widget> list = [];

    for (var s in files ?? []) {
      File file = File(s);

      if (!file.existsSync()) continue;

      list.add(SizedBox(
        width: 80,
        height: 100,
        child: Image.file(file),
      ));
    }

    return list;
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<Widget>>(
        future: buildThumbnails(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting)
            return CircularProgressIndicator();

          return SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: snapshot.data,
            ),
          );
        });
  }
}
