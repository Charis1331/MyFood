package com.example.foodvenueapp.domain.mapper

import com.example.foodvenueapp.api.model.response.APIVenue
import com.example.foodvenueapp.api.model.response.APIVenueLocation
import com.example.foodvenueapp.domain.model.FieldThatCanBeEmpty
import com.example.foodvenueapp.ui.USER_POSITION
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class FoodVenuesMapperTest {

    private val SUT = FoodVenuesMapper

    @Test
    fun mapToDomainFoodVenue_venueIsProperlyMapped() {
        val apiVenues = getFakeAPIVenuesList()

        val venuesList = SUT.mapToDomainFoodVenue(USER_POSITION, apiVenues)

        with(venuesList[0]) {
            assertThat(id).isEqualTo("0")
            assertThat(name).isEqualTo("name0")
            assertThat(address is FieldThatCanBeEmpty.Valid)
            assertThat((address as FieldThatCanBeEmpty.Valid).name).isEqualTo("address0")
            assertThat(category is FieldThatCanBeEmpty.Valid)
            assertThat((category as FieldThatCanBeEmpty.Valid).name).isEqualTo("category0")
            assertThat(coordinates).isEqualTo(LatLng(1.0,1.0))
            assertThat(isSelected).isFalse()
        }
    }

    private fun getFakeAPIVenuesList(): List<APIVenue> =
        listOf(
            APIVenue(
                "0",
                "name0",
                APIVenueLocation("address0", 1.0, 1.0),
                "category0"
            )
        )

}