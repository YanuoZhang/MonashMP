package com.example.monashMP.api

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val weather: List<WeatherDescription>,
    val main: WeatherMain
)

data class WeatherDescription(
    val main: String,
    val description: String
)

data class WeatherMain(
    val temp: Float
)

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
