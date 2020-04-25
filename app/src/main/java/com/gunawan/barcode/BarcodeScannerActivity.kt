package com.gunawan.barcode

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.gunawan.barcode.databinding.ActivityBarcodeScannerBinding

class BarcodeScannerActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 2
    }

    private lateinit var binding: ActivityBarcodeScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_scanner)
        binding.toolbar.title = getString(R.string.scanner_page)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (isCameraPermissionGranted()) {
            binding.tvScan.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder()
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()
        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            val parent = binding.tvScan.parent as ViewGroup
            parent.removeView(binding.tvScan)
            binding.tvScan.surfaceTexture = previewOutput.surfaceTexture
            parent.addView(binding.tvScan, 0)
        }
        val imageAnalysisConfig = ImageAnalysisConfig.Builder()
            .build()
        val imageAnalysis = ImageAnalysis(imageAnalysisConfig)
        val qrCodeAnalyzer = BarcodeAnalyzer { qrCodes ->
            qrCodes.forEach {
                val intent = Intent()
                intent.putExtra("textScan", it.rawValue)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        imageAnalysis.analyzer = qrCodeAnalyzer
        CameraX.bindToLifecycle(this as LifecycleOwner, preview, imageAnalysis)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult (requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                binding.tvScan.post { startCamera() }
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
