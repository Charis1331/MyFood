package com.example.foodvenueapp.api.service

import com.example.foodvenueapp.api.model.deserializer.APIVenuePhotoResponseDeserializer
import com.example.foodvenueapp.api.model.deserializer.APIVenuesResponseDeserializer
import com.example.foodvenueapp.api.model.request.APILocation
import com.example.foodvenueapp.api.model.response.APIVenuePhotoResponse
import com.example.foodvenueapp.api.model.response.APIVenuesResponse
import com.example.foodvenueapp.api.service.interceptor.TokenInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val FOOD_CATEGORY_ID = "4d4b7105d754a06374d81259"

interface FoodVenueService {

    @GET("search?categoryId=$FOOD_CATEGORY_ID")
    suspend fun getVenues(
        @Query("ll") location: APILocation,
        @Query("radius") radius: Float
    ): APIVenuesResponse

    @GET("{venueId}/photos")
    suspend fun getVenuePhotosResponse(@Path("venueId") venueId: String): APIVenuePhotoResponse

    companion object {
        private const val BASE_URL = "https://api.foursquare.com/v2/venues/"

        fun create(): FoodVenueService {
            val client = getClient()
            val gson = getGson()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(FoodVenueService::class.java)
        }

        private fun getClient(): OkHttpClient {
            val logger = HttpLoggingInterceptor().apply {
                level = Level.BODY
            }
            return OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(TokenInterceptor)
                .build()
        }

        private fun getGson(): Gson =
            GsonBuilder()
                .registerTypeAdapter(APIVenuesResponse::class.java, APIVenuesResponseDeserializer)
                .registerTypeAdapter(
                    APIVenuePhotoResponse::class.java,
                    APIVenuePhotoResponseDeserializer
                )
                .create()
    }
}