package com.challange.weather.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.challange.weather.data.model.WeatherDetail.Companion.TABLE_NAME

/**
 * Data class for Database entity and Serialization.
 */
@Entity(tableName = TABLE_NAME)
data class WeatherDetail(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,
    var weatherType: String? = null,
    var temp: Double? = null,
    var icon: String? = null,
    var cityName: String? = null,
    var countryName: String? = null,
    var dateTime: String? = null,
    var speed: Double? = null,
    var pressure: Int? = null,
    var humidity: Int? = null,
    var sunRise: Int? = null,
    var sunSet: Int? = null
) {
    companion object {
        const val TABLE_NAME = "weather_detail"
    }
}