package com.example.foodvenueapp

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodvenueapp.api.service.FoodVenueService
import com.example.foodvenueapp.data.repository.DefaultFoodVenueRepository
import com.example.foodvenueapp.data.repository.FoodVenueRepository
import com.example.foodvenueapp.domain.manipulator.DefaultVenueDataManipulator
import com.example.foodvenueapp.data.source.DataSource
import com.example.foodvenueapp.data.source.RemoteDataSource
import com.example.foodvenueapp.ui.*
import com.example.foodvenueapp.ui.home.map.MapHandler.MapListener
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory
import com.example.foodvenueapp.ui.home.map.CustomMapHandler
import com.example.foodvenueapp.ui.home.map.MapHandler
import com.example.foodvenueapp.util.manager.dialog.AlertDialogManager
import com.example.foodvenueapp.util.manager.dialog.DialogManager
import com.example.foodvenueapp.data.provider.CustomLocationProvider
import com.example.foodvenueapp.data.manager.LocationPermissionsManager
import com.example.foodvenueapp.data.provider.UserLocationProvider
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

interface HasInjectionHelper {
    fun getInjectionHelper(): InjectionHelper
}

abstract class InjectionHelper {

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    abstract fun provideRemoteDataSource(): DataSource

    fun provideMapHandler(listener: MapListener): MapHandler =
        CustomMapHandler(listener)

    fun provideDialogManager(context: Context): DialogManager =
        AlertDialogManager(context)

    fun provideDialogPropertiesFactory(activity: FragmentActivity): DialogPropertiesFactory =
        DialogPropertiesFactory(activity)

    fun provideViewModelFactory(activity: FragmentActivity): ViewModelProvider.Factory =
        ViewModelFactory(provideVenuesRepository(), provideLocationProvider(activity), DefaultVenueDataManipulator())

    private fun provideLocationProvider(activity: FragmentActivity): CustomLocationProvider =
        UserLocationProvider(activity, provideLocationPermissionsManager(activity), locationRequest)

    private fun provideVenuesRepository(): FoodVenueRepository =
        DefaultFoodVenueRepository(provideRemoteDataSource())

    private fun provideLocationPermissionsManager(activity: FragmentActivity) =
        LocationPermissionsManager(activity, locationRequest)
}

@ExperimentalCoroutinesApi
class DefaultInjectionHelper : InjectionHelper() {

    override fun provideRemoteDataSource(): DataSource = RemoteDataSource(FoodVenueService.create(), Dispatchers.Default)

}