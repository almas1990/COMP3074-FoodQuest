package com.comp3074.foodquest.ui

import com.comp3074.foodquest.R

import android.app.Dialog
import android.location.Geocoder
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FullScreenMapFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val root = FrameLayout(context)
        val container = FrameLayout(context).apply { id = R.id.full_map_container }
        root.addView(container, FrameLayout.LayoutParams(-1, -1))
        val close = Button(context).apply {
            text = "Close map"
            setOnClickListener { dismiss() }
        }
        root.addView(close, FrameLayout.LayoutParams(-2, -2).apply { gravity = android.view.Gravity.TOP or android.view.Gravity.END })
        val status = TextView(context).apply {
            id = R.id.full_map_status
            setBackgroundColor(android.graphics.Color.WHITE)
            setPadding(16, 16, 16, 16)
            text = "Finding address..."
        }
        root.addView(status, FrameLayout.LayoutParams(-1, -2).apply { gravity = android.view.Gravity.BOTTOM })
        return Dialog(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen).apply { setContentView(root) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val map = (childFragmentManager.findFragmentByTag("fullmap") as? SupportMapFragment)
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.full_map_container, it, "fullmap").commitNow()
            }
        val address = requireArguments().getString("address").orEmpty()
        val place = requireArguments().getString("place").orEmpty()
        map.getMapAsync { googleMap ->
            if (!isAdded) return@getMapAsync
            lifecycleScope.launch {
                val context = requireContext().applicationContext
                val location = withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        Geocoder(context).getFromLocationName(address, 1)?.firstOrNull()
                    } catch (_: Exception) { null }
                }
                if (!isAdded) return@launch
                dialog?.findViewById<TextView>(R.id.full_map_status)?.text = if (location == null) "Address could not be found. Check your connection and address." else address
                if (location != null) {
                    val point = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(MarkerOptions().position(point).title(place))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16f))
                }
            }
        }
    }

    companion object {
        fun newInstance(place: String, address: String) = FullScreenMapFragment().apply {
            arguments = bundleOf("place" to place, "address" to address)
        }
    }
}
