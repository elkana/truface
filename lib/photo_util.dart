import 'dart:io';

import 'package:image_picker/image_picker.dart';

class PhotoUtil {
  static Future<File> takePhotoGeneric() async {
    final pickedFile = await ImagePicker().getImage(source: ImageSource.camera);

    // File image = await ImagePicker.pickImage(
    //     source: ImageSource.camera, imageQuality: 20);

    if (pickedFile == null) return null;

    File image = File(pickedFile.path);

    int size = await image.length();
    print("takePhoto.Path=${image.path} ($size Bytes)");

    return image;
  }

  static Future<File> takePhotoFromGallery() async {
    final pickedFile =
        await ImagePicker().getImage(source: ImageSource.gallery);

    // File image = await ImagePicker.pickImage(
    //     source: ImageSource.camera, imageQuality: 20);

    if (pickedFile == null) return null;

    File image = File(pickedFile.path);

    int size = await image.length();
    print("takePhoto.Path=${image.path} ($size Bytes)");

    return image;
  }
}
