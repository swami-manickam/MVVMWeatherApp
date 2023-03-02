package com.challange.weather.presentation.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.challange.weather.data.preference.AppPreference
import com.challange.weather.presentation.customviews.CustomProgressDialog
import com.challange.weather.utils.LoggerUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

  private var progressDialog: CustomProgressDialog? = null

  @Inject
  lateinit var appPreference: AppPreference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  fun showLoading() {
    try {
      if (progressDialog == null) {
        progressDialog = CustomProgressDialog.create(context = requireContext(), cancelable = false)
      }
      progressDialog?.show()
    } catch (exception: Exception) {
      LoggerUtils.e("Exception", exception.message)
    }
  }

  fun hideLoading() {
    progressDialog?.hide()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    progressDialog = null
  }
}