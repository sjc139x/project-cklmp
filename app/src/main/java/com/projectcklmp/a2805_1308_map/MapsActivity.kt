package com.projectcklmp.a2805_1308_map

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.text.TextUtils.indexOf
import android.util.Log
import android.widget.Button
import com.google.android.gms.maps.model.*
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.projectcklmp.a2805_1308_map.models.Markers


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var placeMarkerButton: Button
    private lateinit var changeView: Switch
    private val VIDEO_REQUEST = 101
    private var mStorageRef: StorageReference? = null
    private val storage = FirebaseStorage.getInstance()
    private var storageRef = storage.getReference()
    private lateinit var currentUser: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCurrentUser()
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get markers from firebase
        getMarkers()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)
        placeMarkerButton = findViewById(R.id.drop_vid_button)
        changeView = findViewById(R.id.change_detail)

        // Places a marker on the map on the click of a button, centres camera, no zooming
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            placeMarkerButton.setOnClickListener {
                fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        lastLocation = location
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                        startCamera()
                    }
                }
            }
        }

        // Added detail view switch to run a function on change
        changeView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changeMapViewDetail(googleMap)
            } else {
                changeMapViewSimple(googleMap)
            }
        }

        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_less_landmarks
                )
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Goes to your current location and zoom in when you turn the app on
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    // Starts the camera intent
    private fun startCamera() {
        val videoIntent =
            Intent(MediaStore.ACTION_VIDEO_CAPTURE)//starts the capturevideo intent, makes a request to the camera2 api
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST)
        }

    }

    // On successful video confirmation
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                mStorageRef = FirebaseStorage.getInstance().reference
                val file = data.data
                val riversRef = storageRef.child("images/rivers.jpg")
                riversRef.putFile(file!!).addOnSuccessListener {
                    // Get a URL to the uploaded content
                    Log.d("ij", "ijoj")

                }
                // Pin style
                val markerOptions = MarkerOptions().position(currentLatLng).title(currentUser.email)
                markerOptions.icon(
                    BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(resources, R.mipmap.gem_basic)
                    )
                )

                // Add object to firebase
                database = FirebaseDatabase.getInstance()
                databaseReference = database!!.reference!!.child("markers").push()
                auth = FirebaseAuth.getInstance()

                databaseReference.child("user").setValue(currentUser.email)
                databaseReference.child("latLng").setValue(currentLatLng)


                // Add marker to map
                map.addMarker(markerOptions)
            }
        }
    }


    private fun getCurrentUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentUser = user
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show()
        }

    }

    // Show map view without landmarks
    private fun changeMapViewSimple(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_less_landmarks
                )
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }
    }

    // Show map view with landmarks
    private fun changeMapViewDetail(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }
    }


    private fun getMarkers() {
        databaseReference = FirebaseDatabase.getInstance().getReference()
        databaseReference.child("markers").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val markers = snapshot.children
                markers.forEach {
                    val userForMarker = it.child("user").value
                    val latForMarker = it.child("latLng").child("latitude").value
                    val lngForMarker = it.child("latLng").child("longitude").value

                    val places = LatLng(latForMarker as Double, lngForMarker as Double)
                    val markerOptions = MarkerOptions().position(places).title(userForMarker as String?)
                    markerOptions.icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(resources, R.mipmap.gem_basic)
                        )
                    )
                    map.addMarker(markerOptions)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "listener:onCancelled", databaseError.toException())
            }
        })
    }


}
