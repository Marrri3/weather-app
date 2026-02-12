package com.example.weather_app

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

  // Данные о погоде
  private val _weatherData = MutableLiveData<WeatherData>()
  val weatherData: LiveData<WeatherData> = _weatherData

  // Текст ошибки для UI
  private val _errorMessage = MutableLiveData<String>()
  val errorMessage: LiveData<String> = _errorMessage

  // Индикатор загрузки
  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  // API-ключ (в продакшене лучше в BuildConfig)
  private val apiKey = "0d5c5a99e7372b0acca7aae6544c8e06"

  // Запрос погоды по названию города
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
        _errorMessage.value = "Ошибка: ${e.localizedMessage}"
      } finally {
        _isLoading.value = false
      }
    }
  }

  // Запрос погоды по координатам
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
          _errorMessage.value = "Ошибка получения данных"
        }
      } catch (e: Exception) {
        _errorMessage.value = "Ошибка сети: ${e.localizedMessage}"
      } finally {
        _isLoading.value = false
      }
    }
  }
}
