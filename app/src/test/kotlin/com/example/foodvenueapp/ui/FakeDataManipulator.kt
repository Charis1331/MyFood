package com.example.foodvenueapp.ui

import com.example.foodvenueapp.domain.manipulator.PreviousSelectedVenueInfo
import com.example.foodvenueapp.domain.manipulator.VenueDataManipulator
import com.example.foodvenueapp.domain.model.FoodVenue

internal class FakeDataManipulator: VenueDataManipulator {

    override suspend fun getNewListForSelectedVenue(
        info: PreviousSelectedVenueInfo,
        newSelectionId: String
    ): List<FoodVenue> =
         emptyList()

    override suspend fun getListWithUnselectedVenue(info: PreviousSelectedVenueInfo): List<FoodVenue> =
         emptyList()

}