package com.example.foodvenueapp.ui.venues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodvenueapp.data.repository.FoodVenueRepository
import com.example.foodvenueapp.domain.manipulator.PreviousSelectedVenueInfo
import com.example.foodvenueapp.domain.manipulator.VenueDataManipulator
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.domain.model.ResultWrapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VenuesViewModel(
    private val repository: FoodVenueRepository,
    private val manipulator: VenueDataManipulator
) : ViewModel() {

    private val _venues = MutableLiveData<List<FoodVenue>?>()
    val venues: LiveData<List<FoodVenue>?> = _venues

    private val _genericError = MutableLiveData<Int>()
    val genericError = _genericError

    private val _networkError = MutableLiveData<Boolean>()
     val networkError = _networkError

    private val lastPositionOfInterest = MutableLiveData<PositionOfInterest>()

    private val selectedVenueId = MutableLiveData<String?>()

    fun getVenues(userPosition: LatLng, mapPosition: PositionOfInterest) {
        if (mapPosition == lastPositionOfInterest.value) return
        lastPositionOfInterest.value = mapPosition

        viewModelScope.launch {
            repository.getVenues(userPosition, mapPosition).collect { response ->
                when (response) {
                    is ResultWrapper.Success -> {
                        _venues.value = response.data
                        selectedVenueId.value = null

                        _networkError.value = false
                    }
                    is ResultWrapper.Error -> _genericError.value = response.code
                    ResultWrapper.NetworkError -> _networkError.value = true
                }
            }
        }
    }

    fun getOnVenueSelectedList(venueId: String) {
        val venuesList = venues.value
        if (venuesList != null) {
            viewModelScope.launch {
                val info = PreviousSelectedVenueInfo(venuesList, selectedVenueId.value)
                val newList = manipulator.getNewListForSelectedVenue(info, venueId)
                _venues.value = newList
                selectedVenueId.value = venueId
            }
        }
    }

    fun getOnAllVenuesUnelectedList() {
        val venuesList = venues.value
        if (venuesList != null) {
            viewModelScope.launch {
                val info = PreviousSelectedVenueInfo(venuesList, selectedVenueId.value)
                val newVenuesList = manipulator.getListWithUnselectedVenue(info)
                _venues.value = newVenuesList
                selectedVenueId.value = null
            }
        }
    }
}