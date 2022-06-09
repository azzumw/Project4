package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
//import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
//import com.udacity.project4.utils.wrapEspressoIdlingResource
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.properties.Delegates


private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var map: GoogleMap
    private var isPoiSelected by Delegates.notNull<Boolean>()
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

            if (isPoiSelected) {
                onLocationSelected()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.selection_location_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun onLocationSelected() {
        _viewModel.isPoiSelected.value = true
        _viewModel.selectedPOI.value = selectedPoi
        _viewModel.reminderSelectedLocationStr.value = selectedPoi.name

        findNavController().apply {
            navigate(R.id.saveReminderFragment)
            popBackStack(R.id.selectLocationFragment, true)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(gMap: GoogleMap) {
        isPoiSelected = false

        map = gMap

        checkPermissionsAndDeviceLocationSettings()

        setMapStyle(map)
        setPoiClick(map)
        setMapLongClick(map)
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED
    }



    @SuppressLint("MissingPermission")
    private fun checkPermissionsAndDeviceLocationSettings() {
        if (isPermissionGranted()) {
            checkDeviceLocationSettings()
        }
         else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        map.isMyLocationEnabled = true

        val locationResult: Task<Location> = fusedLocationClient.lastLocation
        locationResult.addOnCompleteListener(OnCompleteListener<Location?> { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                if (task.result != null) {
                    val location: Location = task.result!!
                    val currentLatLng = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    val update = CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        18f
                    )
                    map.animateCamera(update)
                }
            }
        })
    }


    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(SaveReminderFragment.TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    activity!!.findViewById<CoordinatorLayout>(R.id.myCoordinatorLayout),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.e(TAG, "SUCCESSFUL!")

                enableLocation()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*
        you need super.onActivityResult() in the host activity for this to be triggered
        * */
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkPermissionsAndDeviceLocationSettings()
            showSnackBar(getString(R.string.selection_location_message))
        }else if (requestCode == REQUEST_PERMISSION_LOCATION){
            checkPermissionsAndDeviceLocationSettings()
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

            if (grantResults.isNotEmpty() && (grantResults[0] == PERMISSION_GRANTED)) {
                checkPermissionsAndDeviceLocationSettings()
                showSnackBar(getString(R.string.selection_location_message))
            } else {

                Snackbar.make(
                    activity!!.findViewById<ConstraintLayout>(R.id.reminderActivityConstraintLayout),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.settings) {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)

                        }
                        startActivityForResult(intent, REQUEST_PERMISSION_LOCATION)
//                    startActivity(Intent().apply {
//                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
                    }.show()
//                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    REQUEST_PERMISSION_LOCATION)

        }


    }


    private fun setMapLongClick(googleMap: GoogleMap) {
        isPoiSelected = false
        googleMap.setOnMapLongClickListener {
            googleMap.clear()

            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.latitude,
                it.longitude
            )

            val marker = MarkerOptions().position(it)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)

            googleMap.addMarker(marker)

            selectedPoi = PointOfInterest(it, marker.title, marker.title)
            isPoiSelected = true
        }

    }


    private fun setPoiClick(googleMap: GoogleMap) {

        isPoiSelected = false
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
        val viewPos = binding.myCoordinatorLayout
        val snackbar = Snackbar.make(viewPos, text, Snackbar.LENGTH_SHORT)
        snackbar.show()

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

    override fun onDestroy() {
        super.onDestroy()
        isPoiSelected = false
    }
}
