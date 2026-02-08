package com.example.weather_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weather.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

  private lateinit var viewModel: WeatherViewModel
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var binding: ActivityMainBinding

  private val LOCATION_PERMISSION_REQUEST_CODE = 100

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    setupListeners()
//    setupObservers()
  }

  private fun setupListeners() {
    binding.searchButton.setOnClickListener {
      val city = binding.cityEditText.text.toString().trim()
      if (city.isNotEmpty()) {
        viewModel.getWeatherByCity(city)
      } else {
        Toast.makeText(this, "Введите название города", Toast.LENGTH_SHORT).show()
      }
    }

    binding.cityEditText.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        binding.searchButton.performClick()
        return@setOnEditorActionListener true
      }
      false
    }

    binding.locationButton.setOnClickListener {
      checkLocationEnabled()
    }
  }

  private fun checkLocationEnabled() {
    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsEnabled && !isNetworkEnabled) {
//      showLocationSettingsDialog()
    } else {
      // GPS включен, запрашиваем разрешение
      requestLocationPermission()
    }
  }

  private fun requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      // получаем местоположение
      // getCurrentLocation()
    } else {
      // Запрашиваем разрешение
      ActivityCompat.requestPermissions(
        this,
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        LOCATION_PERMISSION_REQUEST_CODE
      )
    }
  }
}
