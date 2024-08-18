package com.amigo.picture.camera

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.amigo.picture.R
import java.io.File
import java.util.concurrent.Executors

class CameraPreviewView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    private val TAG = "CameraPreviewView"
    private val mPreviewView: PreviewView
    private val cameraExecutor = ContextCompat.getMainExecutor(context)
    private val surfaceExecutor = Executors.newSingleThreadExecutor()
    private var isBindAll = false

    // Preview
    private val preview by lazy(LazyThreadSafetyMode.NONE) {
        Preview.Builder()
            .build()
    }

    private val imageCapture by lazy(LazyThreadSafetyMode.NONE) {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    init {
        val mRootView = LayoutInflater.from(context).inflate(R.layout.layout_camera_preview, this)
        mPreviewView = mRootView.findViewById(R.id.preview_view)
        preview.setSurfaceProvider(surfaceExecutor, mPreviewView.surfaceProvider)
    }


    /**
     * 启动相机
     */
    @RequiresPermission(android.Manifest.permission.CAMERA)
    fun launchCamera(lifecycleOwner: LifecycleOwner, useFrontCamera: Boolean = false) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider = cameraProviderFuture.get()
                // Select back camera as a default
                val cameraSelector =
                    if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    // Unbind use cases before rebinding
                    if (isBindAll) {
                        cameraProvider.unbindAll()
                    }
                    isBindAll = true
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, imageCapture, preview
                    )
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            }, cameraExecutor
        )
    }

    @RequiresPermission(allOf = [android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_MEDIA_IMAGES])
    fun takePhoto(
        saveFile: File,
        useFlashLight: Boolean = false,
        resultListener: TakePhotoResult? = null
    ) {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(saveFile).build()
        imageCapture.flashMode = if (useFlashLight) FLASH_MODE_ON else FLASH_MODE_AUTO
        imageCapture.takePicture(outputFileOptions, cameraExecutor, object : OnImageSavedCallback {
            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                resultListener?.onSaveImage(result.savedUri, saveFile)
            }

            override fun onError(ex: ImageCaptureException) {
                resultListener?.onError(ex.message)
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }

    fun release() {
        if (isBindAll) {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
        isBindAll = false
    }

    interface TakePhotoResult {
        fun onSaveImage(uri: Uri?, file: File)

        fun onError(msg: String?)

    }

}