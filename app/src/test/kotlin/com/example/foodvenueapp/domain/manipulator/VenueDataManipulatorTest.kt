package com.example.foodvenueapp.domain.manipulator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.foodvenueapp.MainCoroutineRule
import com.example.foodvenueapp.data.source.FOOD_VENUE_LIST
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class VenueDataManipulatorTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val SUT = DefaultVenueDataManipulator(Dispatchers.Main)

    @Test
    fun noPreselectedVenues_getNewListForSelectedVenue_newListWithOneSelectedItem() =
        mainCoroutineRule.runBlockingTest {
            val info = PreviousSelectedVenueInfo(FOOD_VENUE_LIST, null)

            val newList = SUT.getNewListForSelectedVenue(info, "1")

            assertThat(newList[1].isSelected).isTrue()
            assertThat(newList[0].isSelected).isFalse()
        }

    @Test
    fun onePreselectedVenue_getNewListForSelectedVenue_previousSelectionIsFalse_newSelectionIsTrue() =
        mainCoroutineRule.runBlockingTest {
            val listWithSelection = FOOD_VENUE_LIST.toMutableList()
            val previousSelection = listWithSelection[0].copy(isSelected = true)
            listWithSelection[0] = previousSelection
            val info = PreviousSelectedVenueInfo(listWithSelection, "0")

            val newList = SUT.getNewListForSelectedVenue(info, "1")

            assertThat(newList[0].isSelected).isFalse()
            assertThat(newList[1].isSelected).isTrue()
        }

    @Test
    fun onePreselectedVenue_getListWithUnselectedVenue_newListWithNoSelectedItems() =
        mainCoroutineRule.runBlockingTest {
            val listWithSelection = FOOD_VENUE_LIST.toMutableList()
            val previousSelection = listWithSelection[0].copy(isSelected = true)
            listWithSelection[0] = previousSelection
            val info = PreviousSelectedVenueInfo(listWithSelection, "0")

            val newList = SUT.getListWithUnselectedVenue(info)

            assertThat(newList[0].isSelected).isFalse()
            assertThat(newList[1].isSelected).isFalse()
        }

}