package com.example.foodvenueapp.ui.venues

import androidx.recyclerview.widget.DiffUtil
import com.example.foodvenueapp.domain.model.FoodVenue

class VenueDiffCallback(
    private val oldList: List<FoodVenue>,
    private val newList: List<FoodVenue>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val olItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return olItem.imageUrl == newItem.imageUrl &&
                olItem.isSelected == newItem.isSelected &&
                olItem.distance.distanceValue == newItem.distance.distanceValue &&
                olItem.distance.distanceUnit == newItem.distance.distanceUnit
    }
}