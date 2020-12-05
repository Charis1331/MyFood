package com.example.foodvenueapp.api.model.deserializer

import com.example.foodvenueapp.api.model.response.APIVenuesResponse
import com.google.gson.*
import java.lang.reflect.Type

object APIVenuesResponseDeserializer: JsonDeserializer<APIVenuesResponse> {

    private const val RESPONSE_ELEMENT = "response"

    private val gson: Gson by lazy {
        Gson()
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): APIVenuesResponse {
        val response = json.asJsonObject.get(RESPONSE_ELEMENT)
        return gson.fromJson(response, APIVenuesResponse::class.java)
    }
}