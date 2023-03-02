package com.challange.weather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.challange.weather.data.local.dao.WeatherDetailDao
import com.challange.weather.data.model.WeatherDetail

@Database(
    entities = [WeatherDetail::class],
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDetailDao(): WeatherDetailDao

}

