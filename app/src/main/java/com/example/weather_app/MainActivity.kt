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
import com.example.weather_app.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

  private lateinit var viewModel: WeatherViewModel
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private val LOCATION_PERMISSION_REQUEST_CODE = 100
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    setupListeners()
    setupObservers()
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

  private fun setupObservers() {
    viewModel.weatherData.observe(this) { weatherData ->
      weatherData?.let {
        displayWeatherData(it)
      }
    }

    viewModel.errorMessage.observe(this) { error ->
      binding.errorTextView.text = error
      binding.errorTextView.visibility = if (error.isNotEmpty()) View.VISIBLE else View.GONE
    }

    viewModel.isLoading.observe(this) { isLoading ->
      binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
      if (isLoading) {
        binding.weatherContainer.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
      }
    }
  }

  private fun displayWeatherData(weatherData: WeatherData) {
    binding.weatherContainer.visibility = View.VISIBLE

    binding.cityTextView.text = "${weatherData.cityName}, ${weatherData.sys.country}"
    binding.tempTextView.text = "${weatherData.main.temp.toInt()}°C"
    binding.descriptionTextView.text = weatherData.weather[0].description.capitalize()
    binding.feelsLikeTextView.text = "${weatherData.main.feelsLike.toInt()}°C"
    binding.humidityTextView.text = "${weatherData.main.humidity}%"
    binding.pressureTextView.text = "${weatherData.main.pressure} hPa"
    binding.windTextView.text = "${weatherData.wind.speed} м/с"

    // Загрузка иконки погоды
    val iconUrl = "https://openweathermap.org/img/wn/${weatherData.weather[0].icon}@2x.png"
    Glide.with(this)
      .load(iconUrl)
      .into(binding.weatherIcon)
  }

  private fun checkLocationEnabled() {
    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsEnabled && !isNetworkEnabled) {
      // GPS выключен, показываем диалог для включения
      showLocationSettingsDialog()
    } else {
      // GPS включен, запрашиваем разрешение
      requestLocationPermission()
    }
  }

  private fun showLocationSettingsDialog() {
    AlertDialog.Builder(this)
      .setTitle("Включить геолокацию")
      .setMessage("Для определения вашего местоположения необходимо включить GPS или сетевую геолокацию")
      .setPositiveButton("Настройки") { _, _ ->
        // Открываем настройки геолокации
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
      }
      .setNegativeButton("Отмена", null)
      .show()
  }

  private fun requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      // Разрешение уже есть, получаем местоположение
      getCurrentLocation()
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

  private fun getCurrentLocation() {
    // Проверяем разрешения еще раз
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }

    // Показываем сообщение о получении местоположения
    Toast.makeText(this, "Определение местоположения...", Toast.LENGTH_SHORT).show()

    fusedLocationClient.lastLocation
      .addOnSuccessListener { location: Location? ->
        if (location != null) {
          // Локация получена
          println("DEBUG: Получена локация: lat=${location.latitude}, lon=${location.longitude}")
          viewModel.getWeatherByLocation(location)
        } else {
          // Локация null, пробуем запросить новую
          println("DEBUG: Локация null, запрашиваем новую...")
          requestNewLocation()
        }
      }
      .addOnFailureListener { e ->
        println("DEBUG: Ошибка получения локации: ${e.message}")
        Toast.makeText(this, "Ошибка получения местоположения", Toast.LENGTH_SHORT).show()
      }
  }

  private fun requestNewLocation() {
    // Проверяем, есть ли разрешение — если нет → запрашиваем его (это вызовет getCurrentLocation() после одобрения)
    if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED &&
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      requestLocationPermission()   // ← это запустит запрос → onRequestPermissionsResult → getCurrentLocation()
      return
    }

    // Здесь мы уже уверены, что разрешение есть (или было только что получено)
    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
      priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
      interval = 10000L
      fastestInterval = 5000L
      numUpdates = 1
    }

    try {
      fusedLocationClient.requestLocationUpdates(
        locationRequest,
        object : com.google.android.gms.location.LocationCallback() {
          override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            locationResult.lastLocation?.let { location ->
              println("DEBUG: Получена новая локация: lat=${location.latitude}, lon=${location.longitude}")
              viewModel.getWeatherByLocation(location)
            } ?: run {
              Toast.makeText(this@MainActivity, "Не удалось определить местоположение", Toast.LENGTH_SHORT).show()
            }
            fusedLocationClient.removeLocationUpdates(this)
          }
        },
        null
      )
    } catch (e: SecurityException) {
      println("DEBUG: SecurityException в requestLocationUpdates: ${e.message}")
      Toast.makeText(this, "Разрешение на геолокацию отозвано", Toast.LENGTH_SHORT).show()
      // Здесь можно заново вызвать requestLocationPermission(), если нужно
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Разрешение получено
        getCurrentLocation()
      } else {
        Toast.makeText(this, "Разрешение на местоположение отклонено", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
