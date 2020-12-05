package com.example.foodvenueapp.api.model.response

import com.google.gson.annotations.SerializedName

class APIVenuesResponse(
    @SerializedName("venues")
    val venues: List<APIVenue>
)