package com.example.foodvenueapp.util.manager

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastManager {

    private lateinit var toast: Toast

    @SuppressLint("ShowToast")
    fun showToast(context: Context, @StringRes messageResId: Int, args: Int? = null) {
        val message = getMessage(context, messageResId, args)
        if (!ToastManager::toast.isInitialized) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        } else {
            toast.setText(message)
        }
        toast.show()
    }

    private fun getMessage(
        context: Context,
        @StringRes messageResId: Int,
        args: Int? = null
    ): String =
        if (args == null) {
            context.getString(messageResId)
        } else {
            context.getString(messageResId, args)
        }
}