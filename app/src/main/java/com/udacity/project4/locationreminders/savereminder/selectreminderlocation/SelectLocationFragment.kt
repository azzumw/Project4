package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap
    private var isPoiSelected = false
    private lateinit var selectedPoi: PointOfInterest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val REQUEST_PERMISSION_LOCATION = 1

    private val TAG = this.javaClass.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {

            if (isPoiSelected){
                onLocationSelected()
            }else{
                Toast.makeText(context,getString(R.string.select_poi),Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun onLocationSelected() {
        _viewModel.latitude.value = selectedPoi.latLng.latitude
        _viewModel.longitude.value = selectedPoi.latLng.longitude
        _viewModel.isPoiSelected.value = true
        _viewModel.selectedPOI.value = selectedPoi
        _viewModel.reminderSelectedLocationStr.value = selectedPoi.name

        findNavController().apply {
            navigate(R.id.saveReminderFragment)
            popBackStack(R.id.selectLocationFragment,true)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(gMap: GoogleMap) {
        map = gMap

        setMapStyle(map)
        setPoiClick(map)

        showSnackBar(getString(R.string.select_poi))

        enableMyLocation()
    }


    private fun setPoiClick(googleMap: GoogleMap) {

        googleMap.setOnPoiClickListener {
            googleMap.clear()

            googleMap.addMarker(
                MarkerOptions().position(it.latLng).title(it.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            ).apply {
                showInfoWindow()
            }

            selectedPoi = it
            isPoiSelected = true
        }
    }

    private fun showSnackBar(text: String) {
        //work on the position of Snack bar
        val snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)
        snackbar.show()
        snackbar.isGestureInsetBottomIgnored = true

//        val snack: Snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
//        val view = snack.view
//        val params = binding.root.layoutParams as LinearLayout.LayoutParams
//        params.gravity = Gravity.TOP
//        view.layoutParams = params
//        snack.show()
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (isPermissionGranted()) {
                val locationResult: Task<Location> = fusedLocationClient.lastLocation
                locationResult.addOnCompleteListener(OnCompleteListener<Location?> { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        val location: Location? = task.result
                        val currentLatLng = LatLng(
                            location!!.latitude,
                            location.longitude
                        )
                        val update = CameraUpdateFactory.newLatLngZoom(
                            currentLatLng,
                            18f
                        )
                        map.moveCamera(update)
                    }
                })
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun locationEnabled() {
        val locationManager =  requireActivity().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // Return a boolean
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true

            val locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnCompleteListener(OnCompleteListener<Location?> { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    val location: Location? = task.result
                    val currentLatLng = LatLng(
                        location!!.latitude,
                        location.longitude
                    )
                    val update = CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        18f
                    )
                    map.animateCamera(update)
                }
            })


        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //    private fun setMapLongClick(googleMap: GoogleMap) {
//
//        googleMap.setOnMapLongClickListener {
//            val snippet = String.format(
//                Locale.getDefault(),
//                "Lat: %1$.5f, Long: %2$.5f",
//                it.latitude,
//                it.longitude
//            )
//            googleMap.addMarker(
//                MarkerOptions().position(it).title(getString(R.string.dropped_pin)).snippet(snippet)
//            )
//        }
//    }
}
