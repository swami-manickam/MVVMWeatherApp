package com.challange.weather.utils

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Html
import com.challange.weather.R
import com.challange.weather.app.AppController.Companion.getInstance
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import retrofit2.adapter.rxjava.HttpException
import java.io.IOException

object ErrorMessageUtil {
  fun getErrorMessage(throwable: Throwable): String? {
    return when (throwable) {
      is JsonParseException -> {
        //Error In Parsing Json Object.
        getInstance()
          ?.resources
          ?.getString(R.string.error_network_connectivity)
      }
      is MalformedJsonException -> {
        //Received Invalid Json Response.
        getInstance()
          ?.resources
          ?.getString(R.string.error_network_connectivity)
      }
      is IOException -> {
        getInstance()
          ?.resources
          ?.getString(R.string.error_network_connectivity)
      }
      is HttpException -> {
        //Servcer Down or Maximo Error.
        //((HttpException) throwable).code() == 500; server down.
        getInstance()
          ?.resources
          ?.getString(R.string.error_network_connectivity)
      }
      else -> {
        throwable.message
      }
    }
  }

  fun getFormattedErrorMessage(message: String?): String {
    return if (VERSION.SDK_INT >= VERSION_CODES.N) {
      Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT).toString()
    } else {
      Html.fromHtml(message).toString()
    }
  }
}