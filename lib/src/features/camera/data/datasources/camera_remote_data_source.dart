import 'package:flutter/services.dart';
import 'package:urban_lens/core/error/exseptions.dart';

abstract class CameraRemoteDataSource {
  Future<String> captureImage();

  Future<void> initializeCamera();
}

class CameraRemoteDataSourceImpl implements CameraRemoteDataSource {
  // We inject the MethodChannel to make this class testable.
  final MethodChannel methodChannel;

  CameraRemoteDataSourceImpl({
    // Default channel name. We will use this EXACT name in Kotlin later.
    this.methodChannel = const MethodChannel('com.urbanlens/camera'),
  });

  @override
  Future<String> captureImage() async {
    try {
      // We ask native to take the photo and expect a String (path) back.
      final String? imagePath = await methodChannel.invokeMethod<String>('captureImage');

      if (imagePath == null) {
        throw CameraException(message: 'Camera returned null path');
      }

      return imagePath;
    } on PlatformException catch (e) {
      throw CameraException(message: 'Native Capture Failed: ${e.message}');
    }
  }

  @override
  Future<void> initializeCamera() async {
    try {
      await methodChannel.invokeMethod('initialize');
    } on PlatformException catch (e) {
      throw CameraException(message: 'Native Camera Init Failed: ${e.message}');
    }
  }
}
