package com.example.foodvenueapp.util.manager.dialog

import android.content.Context
import android.content.DialogInterface.BUTTON_POSITIVE
import com.example.foodvenueapp.domain.model.DialogProperties
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogManager(private val context: Context) : DialogManager {

    private val dialog = MaterialAlertDialogBuilder(context).create()

    override fun showDialog(properties: DialogProperties) {
        dismissDialog()

        with(properties) {
            dialog.let { d ->
                val message = context.getString(messageResId)
                val positiveButtonMessage = context.getString(positiveButtonMessageResId)

                d.setMessage(message)
                d.setButton(BUTTON_POSITIVE, positiveButtonMessage) { dialog, _ ->
                    positiveButtonAction()
                    dialog.dismiss()
                }
                d.show()
            }
        }
    }

    override fun dismissDialog() =
        dialog.dismiss()
}