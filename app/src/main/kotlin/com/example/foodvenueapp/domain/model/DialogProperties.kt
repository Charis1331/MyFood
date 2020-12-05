package com.example.foodvenueapp.domain.model

import androidx.annotation.StringRes

class DialogProperties(
    @StringRes val messageResId: Int,
    @StringRes val positiveButtonMessageResId: Int = android.R.string.ok,
    val positiveButtonAction: () -> Unit = {}
)