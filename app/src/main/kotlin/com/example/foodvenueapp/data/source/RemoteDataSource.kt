package com.example.foodvenueapp.data.source

import com.example.foodvenueapp.api.model.mapper.APIFoodVenueRequestMapper
import com.example.foodvenueapp.api.model.response.APIVenuesResponse
import com.example.foodvenueapp.api.service.FoodVenueService
import com.example.foodvenueapp.domain.mapper.FoodVenuesMapper
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.domain.model.ResultWrapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class RemoteDataSource(
    private val service: FoodVenueService,
    private val dispatcher: CoroutineDispatcher
) : DataSource {

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        ResultWrapper.Error(code)
                    }
                    else -> {
                        ResultWrapper.Error()
                    }
                }
            }
        }
    }

    override suspend fun getVenues(
        userPosition: LatLng,
        location: PositionOfInterest
    ): Flow<ResultWrapper<List<FoodVenue>>> = flow {
        val resultOfInitialResponse = fetchVenuesWithoutPhotos(userPosition, location)
        emit(resultOfInitialResponse)

        with(resultOfInitialResponse) {
            if (this is ResultWrapper.Success && data is List<FoodVenue>) {
                getVenuesWithPhotoFlow(data).collect { venuesWithPhotoList ->
                    if (venuesWithPhotoList.isNotEmpty()) {
                        val resultMergedList = (venuesWithPhotoList + data).distinctBy { it.id }
                        emit(copy(data = resultMergedList))
                    }
                }
            }
        }
    }

    private suspend fun fetchVenuesWithoutPhotos(
        userPosition: LatLng,
        mapPosition: PositionOfInterest
    ): ResultWrapper<List<FoodVenue>> =
        safeApiCall(dispatcher) {
            val venuesResponse = fetchInitialVenuesResponse(mapPosition)
            FoodVenuesMapper.mapToDomainFoodVenue(userPosition, venuesResponse.venues)
        }

    private suspend fun fetchInitialVenuesResponse(location: PositionOfInterest): APIVenuesResponse {
        val apiLocation = APIFoodVenueRequestMapper.mapToAPILocation(location.position)
        return service.getVenues(apiLocation, location.radius)
    }

    private suspend fun getVenuesWithPhotoFlow(initialList: List<FoodVenue>): Flow<List<FoodVenue>> =
        channelFlow {
            val venuesWithPhoto = mutableListOf<FoodVenue>()
            initialList.forEach {
                val venuePhotoUrl = computeVenuePhotoUrl(it.id) ?: return@forEach
                val newVenueWithPhotoUrl = it.copy(imageUrl = venuePhotoUrl)
                venuesWithPhoto.add(newVenueWithPhotoUrl)
            }
            send(venuesWithPhoto)
        }

    private suspend fun computeVenuePhotoUrl(venueId: String): String? {
        val photoUrlResult = fetchVenuePhotoUrl(venueId)
        return if (photoUrlResult is ResultWrapper.Success) {
            photoUrlResult.data
        } else {
            null
        }
    }

    private suspend fun fetchVenuePhotoUrl(venueId: String): ResultWrapper<String?> =
        safeApiCall(dispatcher) {
            val venuePhotoResponse = service.getVenuePhotosResponse(venueId)
            venuePhotoResponse.apiVenuePhoto?.getUrl()
        }
}