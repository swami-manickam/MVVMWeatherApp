package com.challange.weather.data.response.base

import com.challange.weather.app.AppConstants
import com.google.gson.annotations.SerializedName

class ApiResponse<T> {
    @SerializedName("data")
    val data: T? = null

    @SerializedName("message")
    val message: String = AppConstants.ERROR_MSG_STAY_TUNED

    @SerializedName("success")
    val isSuccess: Boolean = false
}

data class UpdateResponseModel(
    val message: String,
    val isSuccess: Boolean = false
)