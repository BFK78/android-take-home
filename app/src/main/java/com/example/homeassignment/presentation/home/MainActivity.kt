package com.example.homeassignment.presentation.home

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.homeassignment.databinding.ActivityMainBinding
import com.example.homeassignment.presentation.camera.CameraActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.getCurrentLocation()
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
            )
        )

        lifecycleScope.launch {
            viewModel.locationState.collect {
                if (it.location != null) {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    val address =
                        geocoder.getFromLocation(it.location.latitude, it.location.longitude, 1)
                    Log.i("basim", "${address[0].countryName}  ${address[0].subLocality}  ${address[0].locality}")
                    binding.countryValue.text = address[0].countryName
                    binding.localityValue.text = address[0].locality
                    binding.subLocalityValue.text = address[0].subLocality
                }
            }
        }

        binding.button2.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

    }
}