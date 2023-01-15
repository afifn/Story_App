package com.afifny.storysub.ui.main.fragment.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.R
import com.afifny.storysub.data.Result
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.databinding.FragmentMapsBinding
import com.afifny.storysub.viewModel.DataStoreViewModel
import com.afifny.storysub.viewModel.MainViewModelFactory
import com.afifny.storysub.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var dataStoreViewModel: DataStoreViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var location: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireContext())
        )[MapsViewModel::class.java]
        dataStoreViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(requireContext().dataStore))
        )[DataStoreViewModel::class.java]

        dataStoreViewModel.getUserLogin().observe(viewLifecycleOwner) {
            viewModel.getStoryLocation(it.token).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("TAG", "setupViewModel: load data")
                    }
                    is Result.Success -> {
                        val response = result.data
                        response?.listStory.let {
                            for (story in response?.listStory!!) {
                                val lat: Double = story.lat!!
                                val lon: Double = story.lon!!
                                val latLng = LatLng(lat, lon)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .icon(vectorToBitmap(R.drawable.ic_map))
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocation.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                    moveCamera()
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.location_message_activated),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            setupViewModel()
        }
        setMapStyle()
    }

    private fun moveCamera() {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
    }

    private fun vectorToBitmap(@DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e("TAG", "setMapStyle: failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }
    }
}