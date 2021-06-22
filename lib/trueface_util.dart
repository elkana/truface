import 'dart:convert';
import 'dart:io';

import 'package:flutter/services.dart';

import 'photo_util.dart';
import 'trn_enroll.dart';

class TruefaceUtil {
  static const platform = const MethodChannel("flutter.mc.channel");

  static Future<String> takeSelfieAndScan() async {
    File file = await PhotoUtil.takePhotoGeneric();

    if (file == null) return null;

    dynamic ret = await platform.invokeMethod('truface.check.face', {
      'file': file.path,
    });

    return ret;
  }

  static Future<String> scanPhoto(File file) async {
    dynamic ret = await platform.invokeMethod('truface.check.face', {
      'file': file.path,
    });

    return ret;
  }

  // due to android startactivityforresult, need to pass custom methodchannel
  static Future<String> livePhoto(MethodChannel mc, double scoreSimilarity) async {
    dynamic ret = await mc.invokeMethod('truface.camera.live', {
      // 'file': file.path,
      'score.minimum': '$scoreSimilarity',
    });

    return ret;
  }

  static Future<List<TrnEnroll>> getAllEnroll() async {
    dynamic result = await platform.invokeMethod("truface.group.face");
    // dynamic result = await platform.invokeMethod("truface.group.face", {
    //   'group': 'elkana',
    // });

    print('getAllEnroll terima $result');

    List<TrnEnroll> rows = [];

    List<dynamic> ss = jsonDecode(result);
    for (int i = 0; i < ss.length; i++) {
      TrnEnroll value = TrnEnroll.fromJson(ss[i]);

      rows.add(value);
    }

    return rows;
  }

  static Future<bool> takeSelfieAndEnroll(String groupName) async {
    File file = await PhotoUtil.takePhotoGeneric();

    return enrollPhoto(file, 'elkana');
  }

  static Future<bool> enrollPhoto(File file, String groupName) async {
    if (file == null) return false;

    dynamic ret = await platform.invokeMethod('truface.enroll.face', {
      'file': file.path,
      'group': groupName,
    });

    print('enrollPhoto terima $ret');

    return true;
  }

  static Future<bool> delete(String uid) async {
    dynamic ret = await platform.invokeMethod('truface.delete.uid', {
      'uid': uid,
    });

    print('delete terima $ret');

    return true;
  }

  static Future<bool> clearAll() async {
    dynamic ret = await platform.invokeMethod('truface.clear');

    print('clearAll terima $ret');

    return true;
  }
}
