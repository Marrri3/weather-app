package com.example.weather_app

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
  @GET("weather")
  suspend fun getWeather(
    @Query("q") city: String,
    @Query("appid") apiKey: String,
    @Query("units") units: String = "metric",
    @Query("lang") lang: String = "ru"
  ): Response<WeatherData>

  @GET("weather")
  suspend fun getWeatherByLocation(
    @Query("lat") lat: Double,
    @Query("lon") lon: Double,
    @Query("appid") apiKey: String,
    @Query("units") units: String = "metric",
    @Query("lang") lang: String = "ru"
  ): Response<WeatherData>
}
