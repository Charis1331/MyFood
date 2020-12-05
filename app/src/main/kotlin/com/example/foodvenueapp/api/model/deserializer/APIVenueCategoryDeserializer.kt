package com.example.foodvenueapp.api.model.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object APIVenueCategoryDeserializer: JsonDeserializer<String> {

    private const val NAME_ELEMENT = "name"

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String? {
        val categoryObject = json.asJsonArray.firstOrNull()?.asJsonObject
        return categoryObject?.get(NAME_ELEMENT)?.asString
    }
}