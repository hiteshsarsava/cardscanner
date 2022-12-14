package com.hitesh.mylibrary.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.hitesh.mylibrary.R
import com.hitesh.mylibrary.databinding.ActivityScannerBinding
import com.hitesh.mylibrary.usecases.ExtractDataFromFrameUseCase
import com.hitesh.mylibrary.utils.getCameraProvider
import com.hitesh.mylibrary.utils.hasPermission
import com.hitesh.mylibrary.utils.launchWhenResumed
import permissions.dispatcher.RuntimePermissions
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@RuntimePermissions
class ScannerActivity : AppCompatActivity() {

//    private val useCase =
//        ExtractDataFromImageUseCase(TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS))

    @ExperimentalGetImage
    private val useCaseFromFrame =
        ExtractDataFromFrameUseCase(TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS))

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    @ExperimentalGetImage
    private val framesAnalyzer: ImageAnalysis.Analyzer by lazy {
        ImageAnalysis.Analyzer {
            launchWhenResumed {
                val card = useCaseFromFrame(it) ?: return@launchWhenResumed
                setResult(Activity.RESULT_OK, Intent().putExtra(CARD_DETAILS, card))
                finish()
            }
        }
    }

    private lateinit var binding: ActivityScannerBinding
    private lateinit var camera: Camera
    private var isFlashON = false

    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraPermissions()
    }

    @ExperimentalGetImage
    private fun cameraPermissions() {
        if (hasPermission(Manifest.permission.CAMERA)) {
            launchWhenResumed {
                bindUseCases(getCameraProvider())
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
            }
        }
    }

    @ExperimentalGetImage
    private fun bindUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = buildPreview()
//        val takePicture = buildTakePicture()
        val cameraSelector = buildCameraSelector()

        val imageAnalyzer = buildImageAnalyser()

        val useCaseGroup = UseCaseGroup.Builder().run {
            addUseCase(preview)
            addUseCase(imageAnalyzer)
//            addUseCase(takePicture)
            binding.previewView.viewPort?.let { setViewPort(it) }
            build()
        }

        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
        binding.btnFlash.setOnClickListener {

            if (isFlashON) {
                isFlashON = false
                camera.cameraControl.enableTorch(false)
                binding.btnFlash.setImageResource(R.drawable.ic_baseline_flashlight_on_24)
            } else {
                isFlashON = true
                camera.cameraControl.enableTorch(true)
                binding.btnFlash.setImageResource(R.drawable.ic_baseline_flashlight_off_24)
            }
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun buildImageAnalyser(): ImageAnalysis = ImageAnalysis.Builder()
        .setTargetRotation(Surface.ROTATION_0)
        .setTargetAspectRatio(aspectRatio(binding.previewView.width, binding.previewView.height))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also { it.setAnalyzer(cameraExecutor, framesAnalyzer) }

    private fun buildPreview(): Preview = Preview.Builder()
        .setTargetRotation(Surface.ROTATION_0)
        .setTargetAspectRatio(aspectRatio(binding.previewView.width, binding.previewView.height))
        .build()
        .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

    private fun buildCameraSelector(): CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

//    private fun buildTakePicture(): ImageCapture = ImageCapture.Builder()
//        .setTargetRotation(binding.previewView.display.rotation)
//        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//        .build()

    companion object {
        const val CARD_DETAILS: String = "cardDetails"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val CAMERA_PERMISSION = 7762
    }

    @ExperimentalGetImage
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchWhenResumed {
                bindUseCases(getCameraProvider())
            }
        }
    }
}