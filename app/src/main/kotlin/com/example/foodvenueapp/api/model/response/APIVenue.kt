package com.example.foodvenueapp.api.model.response

import com.example.foodvenueapp.api.model.deserializer.APIVenueCategoryDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

class APIVenue(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: APIVenueLocation,
    @SerializedName("categories")
    @JsonAdapter(APIVenueCategoryDeserializer::class)
    val category: String
) {
    var imageUrl: String? = null
}

class APIVenueLocation(
    @SerializedName("address")
    val address: String?,
    @SerializedName("lat")
    val latitude: Double,
    @SerializedName("lng")
    val longitude: Double,
)