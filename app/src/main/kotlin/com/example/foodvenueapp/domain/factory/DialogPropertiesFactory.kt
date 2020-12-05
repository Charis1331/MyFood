package com.example.foodvenueapp.domain.factory

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.example.foodvenueapp.R
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory.DialogCase.LocationPermission
import com.example.foodvenueapp.domain.factory.DialogPropertiesFactory.DialogCase.LocationSettings
import com.example.foodvenueapp.domain.model.DialogProperties

private typealias StringRes = Int

class DialogPropertiesFactory(private val activity: FragmentActivity) {

    fun create(case: DialogCase): DialogProperties =
        DialogProperties(
            messageResId = getMessageResIdForCase(case),
            positiveButtonAction = getPositiveButtonActionForCase(case)
        )

    private fun getMessageResIdForCase(case: DialogCase): StringRes =
        when (case) {
            is LocationPermission -> R.string.rejected_permissions_message
            LocationSettings -> R.string.rejected_location_settings_message
        }

    private fun getPositiveButtonActionForCase(case: DialogCase) =
        when (case) {
            is LocationPermission -> positiveButtonActionForLocationPermissionCase(case.packageName)
            LocationSettings -> ::positiveButtonActionForLocationSettingsCase
        }

    private fun positiveButtonActionForLocationPermissionCase(packageName: String): () -> Unit =
        {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts(APPLICATION_SETTINGS_URI_SCHEME, packageName, null)
            intent.data = uri
            launchIntentAndFinishActivity(intent)
        }

    private fun positiveButtonActionForLocationSettingsCase() {
        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        launchIntentAndFinishActivity(intent)
    }

    private fun launchIntentAndFinishActivity(intent: Intent) =
        with(activity) {
            startActivity(intent)
            finish()
        }

    sealed class DialogCase {
        class LocationPermission(val packageName: String) : DialogCase()
        object LocationSettings : DialogCase()
    }

    private companion object {
        private const val APPLICATION_SETTINGS_URI_SCHEME = "package"
    }

}

