package com.example.foodvenueapp.ui.venues

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.foodvenueapp.R
import com.example.foodvenueapp.databinding.FragmentVenuesBinding
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.util.gone
import com.example.foodvenueapp.util.injectionHelper
import com.example.foodvenueapp.util.manager.ToastManager
import com.example.foodvenueapp.util.visible
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VenuesFragment : Fragment(), VenuesAdapter.OnItemClickListener {

    private lateinit var venueSelectedListener: VenuesListener

    private lateinit var viewModel: VenuesViewModel
    private var fetchVenuesJob: Job? = null

    private val venuesAdapter: VenuesAdapter = VenuesAdapter(this)

    private var _binding: FragmentVenuesBinding? = null
    private val binding get() = _binding!!

    override fun onItemClick(venue: FoodVenue) {
        venueSelectedListener.onVenueSelected(venue)

        viewModel.getOnVenueSelectedList(venue.id)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            venueSelectedListener = context as VenuesListener
        } catch (e: Exception) {
            Log.e(
                "VenuesFragment",
                "Activity ${context as Activity} should implement VenuesListener!"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val injectionHelper = requireActivity().injectionHelper()
        viewModel =
            ViewModelProvider(this, injectionHelper.provideViewModelFactory(requireActivity()))
                .get(VenuesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVenuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        registerObservers()
    }

    private fun setupUI() {
        with(binding) {
            recyclerView.adapter = venuesAdapter
        }
    }

    private fun registerObservers() =
        with(viewModel) {
            observeVenues()
            observeGenericError()
            observeNetworkError()
        }

    private fun VenuesViewModel.observeVenues() =
        venues.observe(viewLifecycleOwner) { venues ->
            if (venues.isNullOrEmpty()) {
                showEmptyVenuesUI()
            } else {
                showVenuesUI()
                venuesAdapter.setVenues(venues)
            }
        }

    private fun showEmptyVenuesUI() =
        with(binding) {
            recyclerView.gone()
            emptyVenuesText.visible()
        }

    private fun showVenuesUI() =
        with(binding) {
            recyclerView.visible()
            emptyVenuesText.gone()
        }

    private fun VenuesViewModel.observeGenericError() =
        genericError.observe(viewLifecycleOwner) { errorCode ->
            showToast(R.string.generic_error, errorCode)
            binding.recyclerView.gone()
        }

    private fun VenuesViewModel.observeNetworkError() =
        networkError.observe(viewLifecycleOwner) { networkErrorOccurred ->
            if (networkErrorOccurred) {
                showToast(messageResId = R.string.no_network_connection)
            }
        }

    private fun showToast(@StringRes messageResId: Int, args: Int? = null) =
        ToastManager.showToast(requireContext(), messageResId, args)

    fun requestVenues(userLocation: LatLng, mapPosition: PositionOfInterest) {
        fetchVenuesJob?.cancel()
        fetchVenuesJob = lifecycleScope.launch {
            viewModel.getVenues(userLocation, mapPosition)
        }
    }

    fun unSelectAllVenues() =
        viewModel.getOnAllVenuesUnelectedList()

    interface VenuesListener {
        fun onVenueSelected(venue: FoodVenue)
    }
}