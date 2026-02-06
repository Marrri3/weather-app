package com.example.weather_app

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
  private val _weatherData = MutableLiveData<WeatherData>()
  val weatherData: LiveData<WeatherData> = _weatherData

  private val _errorMessage = MutableLiveData<String>()
  val errorMessage: LiveData<String> = _errorMessage

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  private val apiKey = "0d5c5a99e7372b0acca7aae6544c8e06"

  fun getWeatherByCity(city: String) {
    _isLoading.value = true
    viewModelScope.launch {
      try {
        val response = RetrofitClient.weatherApi.getWeather(city, apiKey)
        if (response.isSuccessful) {
          _weatherData.value = response.body()
          _errorMessage.value = ""
        } else {
          _errorMessage.value = "Город не найден"
        }
      } catch (e: Exception) {
        _errorMessage.value = "Ошибка соединения: ${e.message}"
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun getWeatherByLocation(location: Location) {
    _isLoading.value = true
    viewModelScope.launch {
      try {
        val response = RetrofitClient.weatherApi.getWeatherByLocation(
          location.latitude,
          location.longitude,
          apiKey
        )
        if (response.isSuccessful) {
          _weatherData.value = response.body()
          _errorMessage.value = ""
        } else {
          _errorMessage.value = "Не удалось получить погоду"
        }
      } catch (e: Exception) {
        _errorMessage.value = "Ошибка соединения: ${e.message}"
      } finally {
        _isLoading.value = false
      }
    }
  }
}
