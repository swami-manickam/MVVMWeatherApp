package com.challange.weather.app

import android.app.Application
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class AppController : Application() {

  companion object {
    private var appController: AppController? = null

    @JvmStatic
    @Synchronized
    fun getInstance(): AppController? {
      return appController
    }
  }

  override fun onCreate() {
    super.onCreate()
    appController = this
    MultiDex.install(this)
  }
}