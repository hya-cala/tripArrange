package com.example.totravel.api

class Repository(private val api: WeatherApi) {

    suspend fun fetchWeather(location: String, apiID: String): CurrentWeatherResponse {

        // Return the result
        return api.getCurrentWeatherData(location, apiID)

    }
}