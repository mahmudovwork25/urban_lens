package com.example.urban_lens

import android.os.Bundle
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.TextureRegistry


class MainActivity : FlutterActivity() {
    // 1. Define the Channel Name.
    // This MUST match the string in your Dart CameraRemoteDataSourceImpl exactly.
    private val CHANNEL = "com.urbanlens/camera"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // 2. Set up the MethodChannel
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, CHANNEL
        ).setMethodCallHandler { call, result ->
            // This block runs on the Android Main Thread whenever Dart invokes a method.

            when (call.method) {
                "initialize" -> {
                    // TODO: Initialize CameraX later
                    println("Android: Camera Initialized (Mock)")
                    result.success(null)
                }

                "captureImage" -> {
                    // TODO: Capture real photo later
                    println("Android: Capture Request Received")

                    // For now, return a fake path to prove the connection works.
                    val mockPath = "/storage/emulated/0/DCIM/test_photo.jpg"
                    result.success(mockPath)
                }

                else -> {
                    // If Dart calls a method we didn't define, tell them.
                    result.notImplemented()
                }
            }
        }
    }
}