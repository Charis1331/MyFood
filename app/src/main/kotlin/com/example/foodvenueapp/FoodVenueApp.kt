package com.example.foodvenueapp

import android.app.Application
import kotlin.contracts.ExperimentalContracts

class FoodVenueApp: Application(), HasInjectionHelper {
@ExperimentalContracts
    override fun getInjectionHelper(): InjectionHelper =
        DefaultInjectionHelper()

}