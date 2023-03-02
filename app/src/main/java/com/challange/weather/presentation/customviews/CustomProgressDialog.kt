package com.challange.weather.presentation.customviews

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.challange.weather.databinding.LayoutCustomProgressDialogBinding

class CustomProgressDialog(context: Context) : AlertDialog(context) {

    private var binding: LayoutCustomProgressDialogBinding

    companion object {
        fun create(context: Context, cancelable: Boolean): CustomProgressDialog {
            val dialog = CustomProgressDialog(context)
            dialog.setCanceledOnTouchOutside(cancelable)
            dialog.setCancelable(cancelable)
            return dialog
        }
    }

    init {
        binding =
            LayoutCustomProgressDialogBinding.inflate(LayoutInflater.from(context), null, false)
        setView(binding.root)
    }

    fun showLoading() {
        this.show()
    }

    fun hideLoading() {
        this.dismiss()
    }

}