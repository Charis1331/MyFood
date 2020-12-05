package com.example.foodvenueapp.api.model.request

data class APILocation(val latitude: Double, val longitude: Double) {

    override fun toString(): String =
        String.format(TO_STRING_FORMAT, latitude, longitude)

    private companion object {
       private const val TO_STRING_FORMAT = "%s,%s"
    }
}