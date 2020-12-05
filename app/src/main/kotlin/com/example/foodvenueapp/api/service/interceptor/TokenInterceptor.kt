package com.example.foodvenueapp.api.service.interceptor

import okhttp3.Interceptor
import okhttp3.Response

object TokenInterceptor : Interceptor {

    private const val CLIENT_ID = "YOUR_FOURSQUARE_CLIENT_ID"
    private const val CLIENT_SECRET = "YOUR_FOURSQUARE_CLIENT_SECRET"
    private const val VERSION = "20201010"

    private const val PARAMETER_NAME_CLIENT_ID = "client_id"
    private const val PARAMETER_NAME_CLIENT_SECRET = "client_secret"
    private const val PARAMETER_NAME_VERSION = "v"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrlBuilder = originalRequest.url.newBuilder()

        val newUrl = originalUrlBuilder.apply {
            addQueryParameter(PARAMETER_NAME_CLIENT_ID, CLIENT_ID)
            addQueryParameter(PARAMETER_NAME_CLIENT_SECRET, CLIENT_SECRET)
            addQueryParameter(PARAMETER_NAME_VERSION, VERSION)
        }.build()

        val newRequestBuilder = originalRequest.newBuilder().url(newUrl)
        val newRequest = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }

}