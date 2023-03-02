package com.challange.weather.data.response.base

import com.challange.weather.data.response.base.ResponseStatus.ERROR
import com.challange.weather.data.response.base.ResponseStatus.FAIL
import com.challange.weather.data.response.base.ResponseStatus.SUCCESS

sealed class ResponseState<out R> {
  data class Loading<T>(val isLoading: T) : ResponseState<T>()
  data class Success<T>(val data: T) : ResponseState<T>()
  data class Failure(val error: Throwable) : ResponseState<Throwable>()
}

class AppResponse<T> private constructor(
  val status: Int,
  val data: T?,
  val throwable: Throwable?
) {
  companion object {

    fun <T> success(data: T): AppResponse<T> {
      return AppResponse(SUCCESS, data, null)
    }

    fun <T> failure(data: T): AppResponse<T> {
      return AppResponse(FAIL, data, null)
    }

    fun error(error: Throwable): AppResponse<*> {
      return AppResponse(ERROR, null, error)
    }
  }
}
