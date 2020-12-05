package com.example.foodvenueapp.data.manager

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.fragment.app.FragmentActivity
import com.example.foodvenueapp.data.manager.LocationPermissionResult.Granted
import com.example.foodvenueapp.data.manager.LocationPermissionResult.Rejected
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest
import kotlin.coroutines.resume

class LocationPermissionsManager(
    private val activity: FragmentActivity,
    private val locationRequest: LocationRequest
) : RuntimePermissionsManager {

    override suspend fun requestLocationPermissions(): LocationPermissionResult =
        suspendCancellableCoroutine { cont ->
            val grantedAction = { cont.resume(Granted) }
            val rejectPermissionsAction = { cont.resume(Rejected.RejectedPermission) }
            val rejectLocationSettingsAction = { cont.resume(Rejected.RejectedLocationSettings) }

            val askForPermissionRequest = activity.constructLocationPermissionRequest(
                permissions = arrayOf(LocationPermission.FINE),
                onPermissionDenied = rejectPermissionsAction,
                onNeverAskAgain = rejectPermissionsAction,
                onShowRationale = { rejectPermissionsAction() }
            ) {
                promptUserToChangeSettingsIfNeeded(grantedAction, rejectLocationSettingsAction)
            }

            cont.invokeOnCancellation {
                rejectPermissionsAction()
            }

            askForPermissionRequest.launch()
        }

    private fun promptUserToChangeSettingsIfNeeded(
        doOnSucceedToChangeSettings: () -> Unit,
        doOnFailToChangeSettings: () -> Unit
    ) = getDeviceMeetsLocationSettingsTask().addOnCompleteListener { task ->
        if (needsToShowPrompt(task)) {
            val intentSender =
                (task.exception as ResolvableApiException).resolution.intentSender
            showLocationSettingsDialog(
                intentSender,
                doOnSucceedToChangeSettings,
                doOnFailToChangeSettings
            )
        } else {
            doOnSucceedToChangeSettings.invoke()
        }
    }

    private fun getDeviceMeetsLocationSettingsTask(): Task<LocationSettingsResponse> =
        getLocationSettingsClient().checkLocationSettings(getLocationSettingsRequest())

    private fun getLocationSettingsRequest(): LocationSettingsRequest =
        LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

    private fun getLocationSettingsClient(): SettingsClient =
        LocationServices.getSettingsClient(activity)

    private fun needsToShowPrompt(task: Task<LocationSettingsResponse>): Boolean =
        with(task) {
            !isSuccessful && exception is ResolvableApiException
        }

    private fun showLocationSettingsDialog(
        intentSender: IntentSender,
        doOnSucceedToChangeSettings: () -> Unit,
        doOnFailToChangeSettings: () -> Unit
    ) =
        try {
            launchActivityResultLauncher(
                intentSender,
                doOnSucceedToChangeSettings,
                doOnFailToChangeSettings
            )
        } catch (sendEx: IntentSender.SendIntentException) {
            // Ignore the error.
        }

    private fun launchActivityResultLauncher(
        intentSender: IntentSender,
        doOnSucceedToChangeSettings: () -> Unit,
        doOnFailToChangeSettings: () -> Unit
    ) {
        val intentSenderRequest = getIntentSenderRequest(
            intentSender
        )
        getActivityResultLauncher(doOnSucceedToChangeSettings, doOnFailToChangeSettings).launch(
            intentSenderRequest
        )
    }

    private fun getIntentSenderRequest(
        intentSender: IntentSender,
    ): IntentSenderRequest =
        IntentSenderRequest.Builder(intentSender)
            .build()

    private fun getActivityResultLauncher(
        doOnSucceedToChangeSettings: () -> Unit,
        doOnFailToChangeSettings: () -> Unit
    ): ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(StartIntentSenderForResult()) {
            val userChangedSettings = it.resultCode == Activity.RESULT_OK
            if (userChangedSettings) {
                doOnSucceedToChangeSettings.invoke()
            } else {
                doOnFailToChangeSettings.invoke()
            }
        }
}

sealed class LocationPermissionResult {
    object Granted : LocationPermissionResult()
    sealed class Rejected : LocationPermissionResult() {
        object RejectedPermission : Rejected()
        object RejectedLocationSettings : Rejected()
    }
}