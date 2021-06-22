import 'dart:io';

import 'package:flutter/material.dart';

import '../photo_util.dart';
import '../trueface_util.dart';

class ScreenAddGroup extends StatefulWidget {
  @override
  _ScreenAddGroupState createState() => _ScreenAddGroupState();

  static void go(BuildContext context, Function(bool) onExit) {
    Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => ScreenAddGroup(
                // contractNo: contractNo,
                ))).then((value) => onExit(value));
  }
}

class _ScreenAddGroupState extends State<ScreenAddGroup> {
  bool _loading = false;
  String _groupName;
  File _selectedFile;
  TextEditingController _controller = new TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Future _submit() async {
    final form = _formKey.currentState;

    if (!form.validate()) return;

    if (_selectedFile == null) {
      print('No Photo selected');
      return;
    }

    form.save();

    setState(() {
      _loading = true;
    });

    try {
      bool ret = await TruefaceUtil.enrollPhoto(_selectedFile, _groupName);

      if (ret) Navigator.pop(context, ret);
    } finally {
      if (this.mounted)
        setState(() {
          _loading = false;
        });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Enroll'),
        actions: [
          ElevatedButton.icon(
            onPressed: _loading ? null : () => _submit(),
            icon: Text('Register'),
            label: Icon(Icons.save_alt_rounded),
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              new TextFormField(
                maxLength: 30,
                controller: _controller,
                decoration: InputDecoration(
                    icon: Icon(Icons.person_add),
                    labelText: "Group Name",
                    hintText: "John Doe"),
                validator: (val) => val.length < 3 ? 'Name too short' : null,
                onSaved: (val) {
                  _groupName = val;
                },
              ),
              SizedBox(height: 10),
              InkWell(
                // child: _selectedFile == null
                //     ? Image.asset('images/avatar_dummy3.png')
                //     : CircleAvatar(
                //         radius: 100,
                //         backgroundImage: FileImage(_selectedFile),
                //       ),
                child: CircleAvatar(
                  radius: 100,
                  backgroundImage: _selectedFile == null
                      ? AssetImage('images/avatar_dummy3.png')
                      : FileImage(_selectedFile),
                ),
                onTap: () async {
                  File file = await PhotoUtil.takePhotoGeneric();

                  if (file == null) return;

                  if (this.mounted)
                    setState(() {
                      _selectedFile = file;
                    });
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
