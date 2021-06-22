import 'package:flutter/material.dart';
import 'package:truface/storage_util.dart';

class ScreenSettings extends StatefulWidget {
  static void go(BuildContext context) {
    Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => ScreenSettings(
                // contractNo: contractNo,
                )));
  }

  @override
  _ScreenSettingsState createState() => _ScreenSettingsState();
}

class _ScreenSettingsState extends State<ScreenSettings> {
  double _selectedValue = 0.7;

  Future firstLoad() async {
    _selectedValue = await StorageUtil.getScoreSimilarity();

    setState(() {});
  }

  @override
  void initState() {
    super.initState();

    firstLoad();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Settings'),
        actions: [
          ElevatedButton.icon(
              onPressed: () async {
                await StorageUtil.setScoreSimilarity(_selectedValue);
              },
              icon: Icon(Icons.save_alt),
              label: Text('Save')),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: [
            Text('$_selectedValue'),
            Slider(
              min: 0.6,
              max: 0.9,
              label: 'Score Minimum',
              divisions: 3,
              value: _selectedValue,
              onChanged: (val) {
                setState(() {
                  _selectedValue = val;
                });
              },
            ),
          ],
        ),
      ),
    );
  }
}
