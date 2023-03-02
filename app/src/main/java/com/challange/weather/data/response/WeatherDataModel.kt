package com.challange.weather.data.response

import com.google.gson.annotations.SerializedName

data class WeatherDataModel(
    @SerializedName("coord") val coord : CoordModel?,
    @SerializedName("weather") val weather : List<WeatherModel>?,
    @SerializedName("base") val base : String?,
    @SerializedName("main") val main : MainModel?,
    @SerializedName("visibility") val visibility : Int?,
    @SerializedName("wind") val wind : WindModel?,
    @SerializedName("rain") val rain : RainModel?,
    @SerializedName("clouds") val clouds : CloudsModel?,
    @SerializedName("dt") val dt : Int?,
    @SerializedName("sys") val sys : SysModel?,
    @SerializedName("timezone") val timezone : String?,
    @SerializedName("id") val id : Int?,
    @SerializedName("name") val name : String?,
    @SerializedName("cod") val cod : Int,
    @SerializedName("message") val message : String?
)

data class CoordModel (
    @SerializedName("lon") val lon : Double?,
    @SerializedName("lat") val lat : Double?
)

data class CloudsModel (
    @SerializedName("all") val all : Int?
)


data class MainModel(
    @SerializedName("temp") val temp : Double?,
    @SerializedName("feels_like") val feels_like : Double?,
    @SerializedName("temp_min") val temp_min : Double?,
    @SerializedName("temp_max") val temp_max : Double?,
    @SerializedName("pressure") val pressure : Int?,
    @SerializedName("humidity") val humidity : Int?,
    @SerializedName("sea_level") val sea_level : Int?,
    @SerializedName("grnd_level") val grnd_level : Int?
)


data class RainModel (
    @SerializedName("1h") val oneHour: Double?
)

data class SysModel (

    @SerializedName("type") val type : Int?,
    @SerializedName("id") val id : Int?,
    @SerializedName("country") val country : String?,
    @SerializedName("sunrise") val sunrise : Int?,
    @SerializedName("sunset") val sunset : Int?
)

data class WeatherModel (
    @SerializedName("id") val id : Int?,
    @SerializedName("main") val main : String?,
    @SerializedName("description") val description : String?,
    @SerializedName("icon") val icon : String?
)

data class WindModel (
    @SerializedName("speed") val speed : Double?,
    @SerializedName("deg") val deg : Int?,
    @SerializedName("gust") val gust : Double?
)
