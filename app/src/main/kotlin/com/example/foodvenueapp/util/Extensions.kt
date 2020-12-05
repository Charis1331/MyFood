package com.example.foodvenueapp.util

import android.app.Activity
import android.location.Location
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.foodvenueapp.HasInjectionHelper
import com.example.foodvenueapp.domain.model.FieldThatCanBeEmpty
import com.google.android.gms.maps.model.LatLng

fun View.visible() {
    isVisible = true
}

fun View.gone() {
    isGone = true
}

fun TextView.setTextOrHideIfEmpty(value: FieldThatCanBeEmpty) =
    when (value) {
        is FieldThatCanBeEmpty.Valid -> text = value.name
        FieldThatCanBeEmpty.Empty -> gone()
    }

fun Activity.injectionHelper() =
    (application as HasInjectionHelper).getInjectionHelper()

fun View.setMargins(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams
        ?: return

    lp.setMargins(
        left ?: lp.leftMargin,
        top ?: lp.topMargin,
        right ?: lp.rightMargin,
        bottom ?: lp.rightMargin
    )

    layoutParams = lp
}

fun LatLng.distanceTo(toLat: Double, toLng: Double): Float {
    val distanceBetween = FloatArray(1)
    Location.distanceBetween(
        latitude,
        longitude,
        toLat,
        toLng,
        distanceBetween
    )
    return distanceBetween[0]
}