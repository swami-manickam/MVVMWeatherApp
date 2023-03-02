package com.challange.weather.data.response.base

import com.challange.weather.app.AppConstants
import com.google.gson.annotations.SerializedName

class ApiListResponse<T> {

    @SerializedName("data")
    val data: List<T>? = null

    @SerializedName("message")
    val message: String = AppConstants.ERROR_MSG_STAY_TUNED

    @SerializedName("success")
    val isSuccess: Boolean = false
}