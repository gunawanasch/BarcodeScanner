package com.gunawan.barcode

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.gunawan.barcode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var REQUEST_SCAN = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.toolbar.title = getString(R.string.app_name)
        binding.btnScan.setOnClickListener {
            startActivityForResult(Intent(this, BarcodeScannerActivity::class.java), REQUEST_SCAN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                binding.textScan = data.getStringExtra("textScan")
            }
        }
    }
}
