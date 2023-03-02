package com.challange.weather.data.request

import com.google.gson.annotations.SerializedName

data class WeatherDataRequest(
    @SerializedName("q")
    val city: String,
    @SerializedName("appid")
    val appId: String
)