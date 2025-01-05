package ru.vasiliiostapenko.weather_les.data

data class WeatherModel(
    val city: String,
    val time: String,
    val temp_c : String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String,
)
