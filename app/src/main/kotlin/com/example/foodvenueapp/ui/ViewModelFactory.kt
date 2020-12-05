package com.example.foodvenueapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodvenueapp.data.provider.CustomLocationProvider
import com.example.foodvenueapp.data.repository.FoodVenueRepository
import com.example.foodvenueapp.domain.manipulator.VenueDataManipulator
import com.example.foodvenueapp.ui.home.MainViewModel
import com.example.foodvenueapp.ui.venues.VenuesViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: FoodVenueRepository,
    private val locationProvider: CustomLocationProvider,
    private val venueDataManipulator: VenueDataManipulator
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(locationProvider) as T
                isAssignableFrom(VenuesViewModel::class.java) ->
                    VenuesViewModel(repository, venueDataManipulator) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}