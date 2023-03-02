package com.challange.weather.presentation.base

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.MenuItem
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.challange.weather.data.preference.AppPreference
import com.challange.weather.utils.LoggerUtils
import com.challange.weather.presentation.customviews.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

  companion object {
    val TAG = BaseActivity::class.java.canonicalName?.toString()
  }

  private var progressDialog: CustomProgressDialog? = null

  @Inject
  lateinit var appPreference: AppPreference


  override fun onCreate(savedInstanceState: Bundle?) {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    super.onCreate(savedInstanceState)
  }

  fun showToast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
  }

  fun showLoading() {
    try {
      if (progressDialog == null) {
        progressDialog = CustomProgressDialog.create(context = this, cancelable = false)
        progressDialog?.show()
      } else {
        progressDialog?.show()
      }
    } catch (exception: Exception) {
      LoggerUtils.e("Exception", exception.message)
    }
  }

  fun hideLoading() {
      progressDialog?.hide()
  }

  fun setHeaderTitle(title: String?) {
    if (supportActionBar != null) supportActionBar!!.title = title
  }

  fun showBackButton(status: Boolean) {
    if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(status)
  }

  fun hideSoftInput() {
    try {
      val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      manager.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
    } catch (e: NullPointerException) {
      LoggerUtils.e(TAG, "Exception in hideSoftInput", e)
    }
  }

  fun setNewFragment(
    containerViewId: Int,
    fragment: BaseFragment,
    addToBackStack: Boolean
  ) {
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(containerViewId, fragment)
    if (addToBackStack) fragmentTransaction.addToBackStack(fragment.javaClass.name)
    fragmentTransaction.commit()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }


  fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  override fun onDestroy() {
    super.onDestroy()
    hideLoading()
    progressDialog = null
  }

}