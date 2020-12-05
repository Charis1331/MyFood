package com.example.foodvenueapp.util.manager.dialog

import com.example.foodvenueapp.domain.model.DialogProperties

interface DialogManager {

    fun showDialog(properties: DialogProperties)

    fun dismissDialog()
}