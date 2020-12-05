package com.example.foodvenueapp.api.model.deserializer

import com.example.foodvenueapp.api.model.response.APIVenuePhoto
import com.example.foodvenueapp.api.model.response.APIVenuePhotoResponse
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object APIVenuePhotoResponseDeserializer : JsonDeserializer<APIVenuePhotoResponse> {

    private const val RESPONSE_ELEMENT = "response"
    private const val PHOTOS_ELEMENT = "photos"
    private const val ITEMS_ELEMENT = "items"

    private val gson: Gson by lazy {
        Gson()
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): APIVenuePhotoResponse {
        val response = json.asJsonObject.getAsJsonObject(RESPONSE_ELEMENT)
        val photosObject = response.getAsJsonObject(PHOTOS_ELEMENT)
        val items = photosObject.getAsJsonArray(ITEMS_ELEMENT)
        val firstItem = items.firstOrNull()
        val item = gson.fromJson(firstItem, APIVenuePhoto::class.java)
        return APIVenuePhotoResponse(item)
    }
}