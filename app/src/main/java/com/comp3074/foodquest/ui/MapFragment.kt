package com.comp3074.foodquest.ui

import com.comp3074.foodquest.R

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.comp3074.foodquest.BuildConfig
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFragment : ScreenFragment() {
    private var googleMap: GoogleMap? = null
    private val permission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        enableLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        back(body)
        heading(body, "Map and directions")
        val quest = model.quests.value.orEmpty().find { it.id == requireArguments().getLong("questId") }
        if (quest == null) { label(body, "Quest is no longer available."); return }
        label(body, quest.place)
        label(body, quest.address)
        action(body, "Get directions") { ExternalActions.directions(requireContext(), quest.address) }
        if (!BuildConfig.MAPS_CONFIGURED) {
            label(body, "Map unavailable. Directions can still be opened.")
            return
        }
        action(body, "Full-screen map") { FullScreenMapFragment.newInstance(quest.place, quest.address).show(childFragmentManager, "fullscreen") }
        val mapContainer = FrameLayout(requireContext()).apply { id = MAP_CONTAINER }
        body.addView(mapContainer, android.widget.LinearLayout.LayoutParams(-1, (420 * resources.displayMetrics.density).toInt()))
        val status = label(body, "Finding address...")
        val mapFragment = (childFragmentManager.findFragmentByTag("map") as? SupportMapFragment)
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(MAP_CONTAINER, it, "map").commitNow()
            }
        val owner = viewLifecycleOwner
        mapFragment.getMapAsync { map ->
            if (!isAdded || owner.lifecycle.currentState == androidx.lifecycle.Lifecycle.State.DESTROYED) return@getMapAsync
            googleMap = map
            enableLocation()
            action(body, "Show my location") {
                permission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            owner.lifecycleScope.launch {
                val context = requireContext().applicationContext
                val location = withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        Geocoder(context).getFromLocationName(quest.address, 1)?.firstOrNull()
                    } catch (_: Exception) { null }
                }
                if (location == null) {
                    status.text = "Address could not be found. Check the address and network connection."
                } else {
                    val point = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(point).title(quest.place))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16f))
                    status.text = quest.address
                }
            }
        }
    }

    private fun enableLocation() {
        val context = context ?: return
        val granted = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
        if (granted) {
            @Suppress("MissingPermission")
            googleMap?.isMyLocationEnabled = true
        }
    }

    override fun onDestroyView() { googleMap = null; super.onDestroyView() }

    companion object {
        private val MAP_CONTAINER = R.id.map_container
        fun newInstance(id: Long) = MapFragment().apply { arguments = bundleOf("questId" to id) }
    }
}
