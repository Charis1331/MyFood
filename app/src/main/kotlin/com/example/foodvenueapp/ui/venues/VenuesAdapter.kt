package com.example.foodvenueapp.ui.venues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foodvenueapp.R
import com.example.foodvenueapp.databinding.ListItemVenueBinding
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.util.gone
import com.example.foodvenueapp.util.setMargins
import com.example.foodvenueapp.util.setTextOrHideIfEmpty
import com.example.foodvenueapp.util.visible
import com.squareup.picasso.Picasso

class VenuesAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<VenuesAdapter.VenueViewHolder>() {

    private val venues: MutableList<FoodVenue> = mutableListOf()

    fun setVenues(newVenuesList: List<FoodVenue>) {
        val diffCallback = VenueDiffCallback(venues, newVenuesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        venues.clear()
        venues.addAll(newVenuesList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ListItemVenueBinding.inflate(inflater, parent, false)
        return VenueViewHolder(itemBinding, itemClickListener)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) =
        holder.bind(venues[position])

    override fun getItemCount(): Int = venues.size

    class VenueViewHolder(
        private val binding: ListItemVenueBinding,
        private val itemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(venue: FoodVenue) {
            with(venue) {

                if (isSelected) {
                    binding.root.gone()
                    binding.root.setMargins(0, 0, 0, 0)
                    binding.root.layoutParams.apply {
                        width = 0
                        height = 0
                    }
                } else {
                    val padding =
                        binding.root.context.resources.getDimension(R.dimen.small_padding).toInt()
                    binding.root.setMargins(padding, padding, padding, padding)
                    binding.root.visible()
                    binding.root.layoutParams.apply {
                        width = ViewGroup.LayoutParams.MATCH_PARENT
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }


                bindImage(imageUrl)

                binding.venueName.text = name

                binding.venueCategory.setTextOrHideIfEmpty(category)

                binding.venueAddress.setTextOrHideIfEmpty(address)

                binding.root.setOnClickListener {
                    itemClickListener.onItemClick(venue)
                }
            }
        }

        private fun bindImage(imageUrl: String?) {
            with(binding.image) {
                if (imageUrl.isNullOrEmpty()) {
                    setImageResource(R.drawable.image_venue_default)
                } else {
                    Picasso.get()
                        .load(imageUrl)
                        .error(R.drawable.image_venue_default)
                        .into(this)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(venue: FoodVenue)
    }
}