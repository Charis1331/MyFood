package com.example.foodvenueapp.ui

import android.app.Application
import android.content.Context
import com.example.foodvenueapp.HasInjectionHelper
import com.example.foodvenueapp.InjectionHelper
import com.example.foodvenueapp.data.source.DataSource
import com.example.foodvenueapp.data.source.FakeDataSource
import com.example.foodvenueapp.data.source.FOOD_VENUE_LIST

class TestApp : Application(), HasInjectionHelper {

    internal var returnError = false
    internal var returnNetworkError = false

    override fun getInjectionHelper(): InjectionHelper =
        TestInjectionHelper(
            applicationContext,
            returnError,
            returnNetworkError
        )
}

class TestInjectionHelper constructor(
    context: Context,
    returnError: Boolean,
    returnNetworkError: Boolean
) : InjectionHelper() {

    private val fakeSource =
        FakeDataSource(FOOD_VENUE_LIST).also {
            it.shouldReturnError = returnError
            it.shouldReturnNetworkError = returnNetworkError
        }

    override fun provideRemoteDataSource(): DataSource =
        fakeSource

}