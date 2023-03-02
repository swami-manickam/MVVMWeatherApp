package com.challange.weather.data.di.modules

import android.content.Context
import androidx.room.Room
import com.challange.weather.data.local.WeatherDatabase


object DatabaseBuilder {


    private var INSTANCE: WeatherDatabase? = null

    fun getInstance(context: Context): WeatherDatabase {
        if (INSTANCE == null) {
            synchronized(WeatherDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            "weather_detail_db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

}