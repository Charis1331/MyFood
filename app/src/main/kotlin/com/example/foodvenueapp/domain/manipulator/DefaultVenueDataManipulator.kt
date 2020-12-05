package com.example.foodvenueapp.domain.manipulator

import com.example.foodvenueapp.domain.model.FoodVenue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultVenueDataManipulator(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) :
    VenueDataManipulator {

    override suspend fun getNewListForSelectedVenue(
        info: PreviousSelectedVenueInfo,
        newSelectionId: String
    ): List<FoodVenue> = withContext(dispatcher) {
        with(info) {
            val newList = currentList.toMutableList()

            if (!previousSelectionId.isNullOrEmpty()) {
                val (indexOfPreviousSelection, updatedPreviousSelection) =
                    getUpdatedPreviousSelection(previousSelectionId, currentList)
                newList[indexOfPreviousSelection] = updatedPreviousSelection
            }

            val (indexOfPreviousSelection, updatedPreviousSelection) =
                getNewSelection(newSelectionId, currentList)
            newList[indexOfPreviousSelection] = updatedPreviousSelection
            newList
        }
    }

    private fun getUpdatedPreviousSelection(
        previousSelectionId: String,
        currentList: List<FoodVenue>
    ): Pair<Int, FoodVenue> {
        val previousSelectionIndex = currentList.indexOfFirst { it.id == previousSelectionId }
        val previousSelection = currentList[previousSelectionIndex]
        return Pair(previousSelectionIndex, previousSelection.copy(isSelected = false))
    }

    private fun getNewSelection(
        newSelectionId: String,
        currentList: List<FoodVenue>
    ): Pair<Int, FoodVenue> {
        val newSelectionIndex = currentList.indexOfFirst { it.id == newSelectionId }
        val newSelection = currentList[newSelectionIndex]
        return Pair(newSelectionIndex, newSelection.copy(isSelected = true))
    }

    override suspend fun getListWithUnselectedVenue(info: PreviousSelectedVenueInfo): List<FoodVenue> =
        withContext(dispatcher) {
            with(info) {
                if (!previousSelectionId.isNullOrEmpty()) {
                    val newList = info.currentList.toMutableList()
                    val (indexOfPreviousSelection, updatedPreviousSelection) =
                        getUpdatedPreviousSelection(previousSelectionId, currentList)
                    newList[indexOfPreviousSelection] = updatedPreviousSelection
                    return@with newList
                }
                currentList
            }
        }
}

class PreviousSelectedVenueInfo(
    val currentList: List<FoodVenue>,
    val previousSelectionId: String?
)