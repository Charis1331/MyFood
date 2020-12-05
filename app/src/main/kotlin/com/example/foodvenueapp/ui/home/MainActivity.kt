package com.example.foodvenueapp.ui.home

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.foodvenueapp.InjectionHelper
import com.example.foodvenueapp.R
import com.example.foodvenueapp.databinding.ActivityMainBinding
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory.DialogCase
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory.DialogCase.LocationPermission
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory.DialogCase.LocationSettings
import com.example.foodvenueapp.domain.model.*
import com.example.foodvenueapp.ui.home.map.MapHandler
import com.example.foodvenueapp.ui.home.map.MapHandler.MapListener
import com.example.foodvenueapp.ui.home.map.MarkerColor
import com.example.foodvenueapp.ui.venues.VenuesFragment
import com.example.foodvenueapp.util.*
import com.example.foodvenueapp.util.manager.ToastManager.showToast
import com.example.foodvenueapp.util.manager.dialog.DialogManager

class MainActivity : AppCompatActivity(), MapListener, VenuesFragment.VenuesListener {

    private lateinit var mapHandler: MapHandler

    private lateinit var dialogManager: DialogManager

    private lateinit var dialogPropertiesFactory: DialogPropertiesFactory

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var venuesFragment: VenuesFragment

    override fun onVenueSelected(venue: FoodVenue) {
        addMarkerWhenOverlayIsShown(venue)
        bindVenueDetails(venue)
    }

    private fun addMarkerWhenOverlayIsShown(venue: FoodVenue) {
        binding.venueDetailsLayout.root.doOnLayout {
            mapHandler.addMarkerAndCenterCameraInAreaWithoutUpdates(
                venue.coordinates,
                getHeightOfVisibleArea(),
                MarkerColor.Other()
            )
        }
    }

    private fun bindVenueDetails(venue: FoodVenue) {
        with(binding) {
            bindDistanceUI(venue.distance)

            bindVenueDetailsUI(venue)

            venueDetailsGroup.visible()
        }
    }

    private fun ActivityMainBinding.bindDistanceUI(distance: FoodVenueDistance) {
        venueDistance.text = distance.distanceValue
        distanceUnit.text = getDistanceUnitText(distance.distanceUnit)
    }

    private fun getDistanceUnitText(unit: DistanceUnit): String =
        when (unit) {
            DistanceUnit.KiloMeters -> resources.getString(R.string.kilometers)
            DistanceUnit.Meters -> resources.getString(R.string.meters)
        }

    private fun ActivityMainBinding.bindVenueDetailsUI(venue: FoodVenue) =
        with(venueDetailsLayout) {
            venueName.text = venue.name
            venueCategory.setTextOrHideIfEmpty(venue.category)
            venueAddress.setTextOrHideIfEmpty(venue.address)
        }

    override fun onMapReady() {
        registerObservers()
        viewModel.getLocation()
    }

    override fun onMapSettled() {
        initializeVenuesFragmentIfNeeded()
        fetchVenues()
    }

    override fun onMapStartedMoving() {
        mapHandler.removeLatestMarkerIfItIsNotInitial()
        binding.venueDetailsGroup.gone()
    }

    private fun fetchVenues() {
        val centerOfVisibleArea =
            mapHandler.getCenterOfVisibleArea(getVisibleAreaDimensions())
        val userPosition = viewModel.userLocation.value
        if (centerOfVisibleArea != null && userPosition != null) {
            val visibleAreaRadius = getVisibleAreaRadius()
            val positionOfInterest = PositionOfInterest(centerOfVisibleArea, visibleAreaRadius)
            venuesFragment.requestVenues(userPosition, positionOfInterest)
        }
    }

    private fun getVisibleAreaDimensions(): AreaDimensions =
        with(binding.fragmentContainer) {
            val w = width + marginStart + marginEnd
            val h = height
            AreaDimensions(w, h)
        }

    private fun getVisibleAreaRadius(): Float =
        with(binding.fragmentContainer.getTopCorners()) {
            mapHandler.getVisibleAreaRadius(left, right)
        }

    private fun initializeVenuesFragmentIfNeeded() {
        if (!::venuesFragment.isInitialized) {
            venuesFragment = supportFragmentManager
                .findFragmentById(R.id.fragmentContainer) as VenuesFragment
        }
    }

    private fun FragmentContainerView.getTopCorners(): ViewCorners {
        val containerY = y.toInt()
        val containerLeftWithoutMargins = left - marginStart
        val containerViewRightWithoutMargins = right + marginEnd

        val topLeftPoint = Point(containerLeftWithoutMargins, containerY)
        val topRightPoint = Point(containerViewRightWithoutMargins, containerY)

        return ViewCorners(topLeftPoint, topRightPoint)
    }

    override fun onBackPressed() {
        if (mapHandler.isCameraInInitialPosition()) {
            super.onBackPressed()
        } else {
            returnMapToInitialState()
        }
    }

    private fun returnMapToInitialState() {
        venuesFragment.unSelectAllVenues()
        mapHandler.moveCameraToInitialPosition()
        binding.venueDetailsGroup.gone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()

        injectDependencies()

        initMapHandler()

        drawBehindStatusBar()
    }

    private fun setupUi() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fitToSystemWindows()
    }

    private fun fitToSystemWindows() =
        with(binding) {
            root.setOnApplyWindowInsetsListener { _, insets ->
                val insetTop = insets.systemWindowInsetTop
                venueDetailsLayout.root.updatePadding(top = insetTop)
                venueDistanceLayout.setMargins(top = insetTop)
                insets
            }
        }

    private fun injectDependencies() {
        val activity = this@MainActivity
        with(injectionHelper()) {
            mapHandler = provideMapHandler(activity)
            dialogManager = provideDialogManager(activity)
            dialogPropertiesFactory = provideDialogPropertiesFactory(activity)
            viewModel = getViewModelProvider().get(MainViewModel::class.java)
        }
    }

    private fun initMapHandler() =
        mapHandler.requestMapToBeDrawn(supportFragmentManager)

    private fun registerObservers() =
        with(viewModel) {
            observeUserLocation()
            observeLocationError()
            observePermissionRejected()
            observeLocationSettingsRejected()
        }

    private fun MainViewModel.observeUserLocation() =
        userLocation.observe(this@MainActivity) { location ->
            mapHandler.addMarkerAndCenterCameraInArea(location, getHeightOfVisibleArea())
        }

    private fun MainViewModel.observeLocationError() =
        locationRetrievalError.observe(this@MainActivity) { showErrorToast ->
            if (showErrorToast) {
                showToast(this@MainActivity, R.string.location_retrieval_error)
            }
        }

    private fun MainViewModel.observePermissionRejected() =
        rejectedPermissions.observe(this@MainActivity) { rejected ->
            if (rejected) {
                showDialog(LocationPermission(packageName))
            } else {
                dismissDialog()
            }
        }

    private fun MainViewModel.observeLocationSettingsRejected() =
        rejectedLocationSettings.observe(this@MainActivity) { rejected ->
            if (rejected) {
                showDialog(LocationSettings)
            } else {
                dismissDialog()
            }
        }

    private fun getHeightOfVisibleArea(): Int = with(binding) {
        fragmentContainer.top - venueDetailsLayout.root.bottom
    }

    private fun showDialog(case: DialogCase) {
        val properties = dialogPropertiesFactory.create(case)
        dialogManager.showDialog(properties)
    }

    private fun dismissDialog() =
        dialogManager.dismissDialog()

    private fun drawBehindStatusBar(): Unit =
        window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

    private fun InjectionHelper.getViewModelProvider(): ViewModelProvider {
        val activity = this@MainActivity
        return ViewModelProvider(activity, provideViewModelFactory(activity))
    }
}