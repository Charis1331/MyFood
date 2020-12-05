package com.example.foodvenueapp.domain.manipulator

import com.example.foodvenueapp.domain.model.FoodVenue

interface VenueDataManipulator {

    suspend fun getNewListForSelectedVenue(
        info: PreviousSelectedVenueInfo,
        newSelectionId: String
    ): List<FoodVenue>

    suspend fun getListWithUnselectedVenue(info: PreviousSelectedVenueInfo): List<FoodVenue>
}