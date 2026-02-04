package com.example.weather_app

import com.google.gson.annotations.SerializedName

data class WeatherData(
  @SerializedName("name") val cityName: String,
  @SerializedName("main") val main: Main,
  @SerializedName("weather") val weather: List<Weather>,
  @SerializedName("wind") val wind: Wind,
  @SerializedName("sys") val sys: Sys
)

data class Main(
  @SerializedName("temp") val temp: Double,
  @SerializedName("feels_like") val feelsLike: Double,
  @SerializedName("pressure") val pressure: Int,
  @SerializedName("humidity") val humidity: Int,
  @SerializedName("temp_min") val tempMin: Double,
  @SerializedName("temp_max") val tempMax: Double
)

data class Weather(
  @SerializedName("id") val id: Int,
  @SerializedName("main") val main: String,
  @SerializedName("description") val description: String,
  @SerializedName("icon") val icon: String
)

data class Wind(
  @SerializedName("speed") val speed: Double,
  @SerializedName("deg") val deg: Int
)

data class Sys(
  @SerializedName("country") val country: String,
  @SerializedName("sunrise") val sunrise: Long,
  @SerializedName("sunset") val sunset: Long
)
