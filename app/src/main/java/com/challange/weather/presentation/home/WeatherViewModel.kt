package com.challange.weather.presentation.home

import androidx.lifecycle.MutableLiveData
import com.challange.weather.app.AppConstants
import com.challange.weather.data.response.WeatherDataModel
import com.challange.weather.data.response.base.ResponseState
import com.challange.weather.data.response.base.ResponseState.*
import com.challange.weather.presentation.base.BaseViewModel
import com.challange.weather.utils.applySchedulers
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor() : BaseViewModel() {


    fun fetchWeatherDetail(cityName: String): MutableLiveData<ResponseState<Any>> {
        val response = MutableLiveData<ResponseState<Any>>()
        val weatherObservable = weatherRepo.getCityWeatherDetail(cityName)

        weatherObservable
            .applySchedulers()
            .doOnSubscribe {
                response.value = Loading(true)
            }
            .doOnTerminate {
                response.value = Loading(false)
            }
            .subscribe({ apiResponse ->
                if (apiResponse.data != null) {
                    response.value = Success(apiResponse.data as WeatherDataModel)
                } else {
                    response.value = Failure(
                        apiResponse.throwable
                            ?: Throwable(AppConstants.ERROR_MSG_STAY_TUNED)
                    )
                }
            }, { throwable ->
                response.value = Failure(throwable)
            })
        return response
    }


    fun fetchWeatherDetailByLatLong(latitude: String,longitude: String): MutableLiveData<ResponseState<Any>> {
        val response = MutableLiveData<ResponseState<Any>>()
        val weatherObservable = weatherRepo.getCityWeatherDetailByLatLong(latitude,longitude)

        weatherObservable
            .applySchedulers()
            .doOnSubscribe {
                response.value = Loading(true)
            }
            .doOnTerminate {
                response.value = Loading(false)
            }
            .subscribe({ apiResponse ->
                if (apiResponse.data != null) {
                    response.value = Success(apiResponse.data as WeatherDataModel)
                } else {
                    response.value = Failure(
                        apiResponse.throwable
                            ?: Throwable(AppConstants.ERROR_MSG_STAY_TUNED)
                    )
                }
            }, { throwable ->
                response.value = Failure(throwable)
            })
        return response
    }







}