package com.challange.weather.presentation.base

import androidx.lifecycle.ViewModel
import com.challange.weather.data.preference.AppPreference
import com.challange.weather.data.repo.WeatherRepo
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    lateinit var weatherRepo: WeatherRepo


    @Inject
    lateinit var appPreference: AppPreference

}
