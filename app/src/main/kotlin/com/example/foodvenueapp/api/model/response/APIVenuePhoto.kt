package com.example.foodvenueapp.api.model.response

import com.google.gson.annotations.SerializedName

class APIVenuePhotoResponse(val apiVenuePhoto: APIVenuePhoto?)

class APIVenuePhoto(
    @SerializedName("prefix")
    private val photoUrlPrefix: String,
    @SerializedName("suffix")
    private val photoUrlSuffix: String
) {

    fun getUrl(): String =
        String.format(TO_STRING_FORMAT, photoUrlPrefix, PREFERRED_WIDTH, PREFERRED_HEIGHT, photoUrlSuffix)

    private companion object {
        private const val TO_STRING_FORMAT = "%s%dx%d%s"

        private const val PREFERRED_WIDTH = 100

        private const val PREFERRED_HEIGHT = 100
    }
}