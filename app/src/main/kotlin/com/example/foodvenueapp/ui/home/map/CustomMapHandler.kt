package com.example.foodvenueapp.ui.home.map

import android.graphics.Point
import android.location.Location
import androidx.fragment.app.FragmentManager
import com.example.foodvenueapp.R
import com.example.foodvenueapp.domain.model.AreaDimensions
import com.example.foodvenueapp.ui.home.map.MapHandler.MapListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

private const val DEFAULT_ZOOM_LEVEL = 15f
private const val MIN_INTERVAL_FOR_NOTIFYING_LISTENERS = 500

class CustomMapHandler(private val listener: MapListener) : MapHandler {

    private lateinit var map: GoogleMap

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var initialCameraPosition: LatLng

    private lateinit var currentCameraPosition: LatLng

    private var initialMarker: Marker? = null

    private var currentMarker: Marker? = null

    private var lastTimeMapIdled = 0L

    override fun requestMapToBeDrawn(fragmentManager: FragmentManager) {
        mapFragment = fragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun addMarkerAndCenterCameraInArea(
        position: LatLng,
        heightOfVisibleArea: Int,
        markerColor: MarkerColor
    ) {
        addMarkerAndCenterCameraInAreaWithoutUpdates(position, heightOfVisibleArea, markerColor)
        listener.onMapSettled()
    }

    override fun addMarkerAndCenterCameraInAreaWithoutUpdates(
        position: LatLng,
        heightOfVisibleArea: Int,
        markerColor: MarkerColor
    ) {
        removeLatestMarkerIfItIsNotInitial()
        addMarker(position, markerColor)
        centerCameraInArea(heightOfVisibleArea)
    }

    override fun getVisibleAreaRadius(bottomLeft: Point, bottomright: Point): Float {
        val bottomLeftCornerOfArea = fromScreenLocation(bottomLeft)
        val rightLeftCornerOfArea = fromScreenLocation(bottomright)
        val distanceBetweenBottomLeftAndRightCorners = FloatArray(1)

        Location.distanceBetween(
            bottomLeftCornerOfArea.latitude,
            bottomLeftCornerOfArea.longitude,
            rightLeftCornerOfArea.latitude,
            rightLeftCornerOfArea.longitude,
            distanceBetweenBottomLeftAndRightCorners
        )
        return distanceBetweenBottomLeftAndRightCorners[0] / 2
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap)
        listener.onMapReady()
    }

    override fun getCenterOfVisibleArea(
        visibleAreaDimensions: AreaDimensions
    ): LatLng? = with(visibleAreaDimensions) {
        val centerPointLocation = Point(width / 2, height / 2)
        fromScreenLocation(centerPointLocation)
    }

    override fun isCameraInInitialPosition(): Boolean =
        initialCameraPosition == currentCameraPosition

    override fun moveCameraToInitialPosition() {
        if (::initialCameraPosition.isInitialized) {
            removeLatestMarkerIfItIsNotInitial()
            animateCamera(initialCameraPosition)
        }
    }

    override fun removeLatestMarkerIfItIsNotInitial() {
        if (initialMarker == currentMarker) return

        currentMarker?.remove()
    }

    private fun addMarker(position: LatLng, markerColor: MarkerColor) =
        with(map) {
            val marker = addMarker(getMarkerOptions(position, markerColor))
            if (initialMarker == null) {
                initialMarker = marker
            }
            currentMarker = marker
            moveCamera(CameraUpdateFactory.newLatLng(position))
        }

    private fun centerCameraInArea(heightOfVisibleArea: Int) {
        val cameraPositionToCenterMarker =
            fromScreenLocation(getCameraLocationInOrderToCenterMarkerInArea(heightOfVisibleArea))
        animateCamera(cameraPositionToCenterMarker)
    }

    private fun animateCamera(cameraPositionToCenterMarker: LatLng) {
        if (!::initialCameraPosition.isInitialized) {
            initialCameraPosition = cameraPositionToCenterMarker
        }
        currentCameraPosition = cameraPositionToCenterMarker
        map.animateCamera(CameraUpdateFactory.newLatLng(cameraPositionToCenterMarker))
    }

    private fun getCameraLocationInOrderToCenterMarkerInArea(heightOfVisibleArea: Int): Point {
        val screenLocationOfMarker = getScreenLocationOfMarker()

        val xOfCameraLocationToCenterMarker = screenLocationOfMarker.x
        val yOfCameraLocationToCenterMarker =
            screenLocationOfMarker.y + heightOfVisibleArea / 2
        return Point(xOfCameraLocationToCenterMarker, yOfCameraLocationToCenterMarker)
    }

    private fun getScreenLocationOfMarker(): Point {
        val markerPosition = map.cameraPosition.target
        return toScreenLocation(markerPosition)
    }

    private fun setupMap(googleMap: GoogleMap) {
        map = googleMap.apply {
            uiSettings.isZoomGesturesEnabled = false
            setMaxZoomPreference(DEFAULT_ZOOM_LEVEL)
            setMinZoomPreference(DEFAULT_ZOOM_LEVEL)
        }
        setMapListeners()
    }

    private fun setMapListeners() =
        map.setOnCameraMoveStartedListener {
            if (shouldNotifyListeners(it)) {
                lastTimeMapIdled = System.currentTimeMillis()

                notifyListeners()
            }
        }

    private fun shouldNotifyListeners(reason: Int): Boolean =
        userInitiatedMove(reason) && minIntervalHasPassed()

    private fun userInitiatedMove(reason: Int): Boolean =
        reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE

    private fun minIntervalHasPassed(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime - lastTimeMapIdled > MIN_INTERVAL_FOR_NOTIFYING_LISTENERS
    }

    private fun notifyListeners() {
        listener.onMapStartedMoving()

        map.setOnCameraIdleListener {
            map.setOnCameraIdleListener(null)
            listener.onMapSettled()
        }
    }

    private fun getMarkerOptions(position: LatLng, markerColor: MarkerColor): MarkerOptions =
        MarkerOptions()
            .position(position)
            .icon(getMarkerBitmap(markerColor))

    private fun getMarkerBitmap(markerColor: MarkerColor): BitmapDescriptor =
        when (markerColor) {
            is MarkerColor.Other -> BitmapDescriptorFactory.defaultMarker(markerColor.hue)
            MarkerColor.Default -> BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black)
        }

    private fun fromScreenLocation(point: Point): LatLng =
        map.projection.fromScreenLocation(point)

    private fun toScreenLocation(position: LatLng): Point =
        map.projection.toScreenLocation(position)

}