package com.challange.weather.data.repo

import com.challange.weather.BuildConfig
import com.challange.weather.data.api.ApiConstants
import com.challange.weather.data.api.WeatherApi
import com.challange.weather.data.response.base.AppResponse
import rx.Observable
import javax.inject.Inject

class WeatherRepo @Inject constructor(
    private val weatherApi: WeatherApi,
) {

    /*------------------------------------Api Implementation Starts------------------------------------*/
    fun getCityWeatherDetail(cityName: String): Observable<AppResponse<*>> {
        return weatherApi.getCityWeatherDetail(
            cityName,
            ApiConstants.WEATHER_METRIC,
            BuildConfig.weather_api_key
        )
            .map { weatherResponse ->
                if (weatherResponse.cod == 200) {
                    AppResponse.success(weatherResponse)
                } else {
                    AppResponse.error(Throwable(message = weatherResponse.message))
                }
            }
    }

    /*------------------------------------Api Implementation Ends------------------------------------*/

}