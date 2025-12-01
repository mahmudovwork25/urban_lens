package com.example.urban_lens

import android.app.Activity
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.TextureRegistry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NativeCameraHandler(
    private val activity: Activity,
    private val textureRegistry: TextureRegistry
) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var textureEntry: TextureRegistry.SurfaceTextureEntry? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun initialize(result: MethodChannel.Result) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            // 1. Get the Camera Provider
            cameraProvider = cameraProviderFuture.get()

            // 2. Create a Flutter Texture Entry
            // This reserves a slot in the GPU that Flutter can read.
            textureEntry = textureRegistry.createSurfaceTexture()
            val surfaceTexture = textureEntry!!.surfaceTexture()

            // Set default resolution (720p is good balance for preview)
            surfaceTexture.setDefaultBufferSize(720, 1280)

            // 3. Define the Preview Use Case
            val preview = Preview.Builder().build()

            // Connect the Preview to the SurfaceTexture
            preview.setSurfaceProvider { request ->
                val surface = Surface(surfaceTexture)
                request.provideSurface(surface, ContextCompat.getMainExecutor(activity)) {
                    // Cleanup when the surface is not needed
                }
            }

            // 4. Define the Image Capture Use Case
            imageCapture = ImageCapture.Builder().build()

            // 5. Bind to Lifecycle
            try {
                cameraProvider!!.unbindAll()

                // We bind both Preview and Capture to the Activity's Lifecycle
                cameraProvider!!.bindToLifecycle(
                    activity as LifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )

                // 6. Return the Texture ID to Flutter
                // This ID is the "key" Flutter needs to display the video feed.
                result.success(textureEntry!!.id())

            } catch (exc: Exception) {
                result.error("CAMERA_INIT_FAILED", exc.message, null)
            }

        }, ContextCompat.getMainExecutor(activity))
    }

    fun takePhoto(result: MethodChannel.Result) {
        val imageCapture = imageCapture ?: run {
            result.error("CAMERA_NOT_INIT", "Camera not initialized", null)
            return
        }

        // Create output file
        val filename = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        val file = File(activity.externalMediaDirs.firstOrNull(), filename)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    result.error("CAPTURE_FAILED", "Photo capture failed: ${exc.message}", null)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Return the absolute path so Dart can display/upload it
                    result.success(file.absolutePath)
                }
            }
        )
    }
}