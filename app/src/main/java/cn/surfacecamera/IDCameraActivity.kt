package cn.surfacecamera

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.io.File


class IDCameraActivity : AppCompatActivity() {

    private val requestCode = 111
    private var isFront = true
    private var picPath = ""
    private var frontPath = ""
    private var backPath = ""
    private lateinit var camera: Camera
    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_camera)
        setStatusBar(this)
        isFront = intent?.getBooleanExtra(IDCardCamera.IS_FRONT, true) ?: true
        frontPath = intent?.getStringExtra(IDCardCamera.PIC_PATH) ?: "${externalCacheDir}/front.jpg"
        backPath = intent?.getStringExtra(IDCardCamera.PIC_PATH) ?: "${externalCacheDir}/back.jpg"
        picPath = if (isFront) frontPath else backPath
        findViewById<TextView>(R.id.cancel).setOnClickListener {
            onBackPressed()
        }
        findViewById<ImageView>(R.id.pic).setImageResource(if (isFront) R.mipmap.camera_idcard_front else R.mipmap.camera_idcard_back)
        previewView = findViewById(R.id.preview)
        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
        findViewById<ImageView>(R.id.cameraTake).setOnClickListener {
            onClick()
        }
        requestPermission()
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        imageCapture.setCropAspectRatio(Rational(4, 3))
        camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    private fun onClick() {
        val outputFileOptions =
            ImageCapture.OutputFileOptions.Builder(File(picPath))
                .build()
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {

                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        application,
                        outputFileResults.savedUri.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setStatusBar(activity: Activity) {
        val decorView: View = activity.window.decorView
        var option: Int = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            option += View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        decorView.setSystemUiVisibility(option)
        activity.window.statusBarColor = Color.TRANSPARENT
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.navigationBarColor =
            ContextCompat.getColor(this, R.color.camera_trans_black)
    }

    private fun requestPermission() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                requestCode
            )
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}