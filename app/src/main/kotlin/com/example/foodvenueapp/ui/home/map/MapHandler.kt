package com.example.foodvenueapp.ui.home.map

import android.graphics.Point
import androidx.fragment.app.FragmentManager
import com.example.foodvenueapp.domain.model.AreaDimensions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

interface MapHandler : OnMapReadyCallback {

    fun requestMapToBeDrawn(fragmentManager: FragmentManager)

    fun addMarkerAndCenterCameraInArea(
        position: LatLng,
        heightOfVisibleArea: Int,
        markerColor: MarkerColor = MarkerColor.Default
    )

    fun addMarkerAndCenterCameraInAreaWithoutUpdates(
        position: LatLng,
        heightOfVisibleArea: Int,
        markerColor: MarkerColor = MarkerColor.Default
    )

    fun getVisibleAreaRadius(bottomLeft: Point, bottomright: Point): Float

    fun getCenterOfVisibleArea(visibleAreaDimensions: AreaDimensions): LatLng?

    fun isCameraInInitialPosition(): Boolean

    fun moveCameraToInitialPosition()

    fun removeLatestMarkerIfItIsNotInitial()

    interface MapListener {
        fun onMapReady()

        fun onMapSettled()

        fun onMapStartedMoving()
    }
}

sealed class MarkerColor {
    class Other(val hue: Float = BitmapDescriptorFactory.HUE_RED) : MarkerColor()
    object Default : MarkerColor()
}