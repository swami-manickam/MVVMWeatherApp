package com.challange.weather.data.api


import com.challange.weather.data.response.WeatherDataModel
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface WeatherApi {

    @GET(ApiConstants.API_WEATHER_INFO)
    fun getCityWeatherDetail(@Query(ApiConstants.CITY) city: String, @Query(ApiConstants.WEATHER_UNIT) unit: String,
                              @Query(ApiConstants.APP_ID) appId: String): Observable<WeatherDataModel>


    @GET(ApiConstants.API_WEATHER_INFO)
    fun getCityWeatherDetailByLatLong(@Query(ApiConstants.LATITUDE) latitude: String,@Query(ApiConstants.LONGITUDE) longitude: String,
                                      @Query(ApiConstants.WEATHER_UNIT) unit: String,
                                      @Query(ApiConstants.APP_ID) appId: String): Observable<WeatherDataModel>

}