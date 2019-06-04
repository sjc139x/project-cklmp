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
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.text.TextUtils.indexOf
import android.util.Log
import android.widget.Button
import com.google.android.gms.maps.model.*
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

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


    override fun onMarkerClick(marker: Marker): Boolean {

val mark = marker.position

        val userloc1 = Location("")
        userloc1.latitude = lastLocation.latitude
        userloc1.longitude = lastLocation.longitude

        val markerloc2 = Location("")
        markerloc2.latitude = mark.latitude
        markerloc2.longitude = mark.longitude

        val distanceInMeters = userloc1.distanceTo(markerloc2)



        val url = "${marker.title}".split(" ")

        Log.d("sausage","${distanceInMeters}")
if (distanceInMeters <= 75) {

    val playIntent = Intent(this, VideoPlayer::class.java)
    playIntent.putExtra("videoUri", url[1]);
    startActivity(playIntent)
}
        else if(distanceInMeters >= 1000) {
    Toast.makeText(this, "U R ${Math.round(distanceInMeters/100)/10}km from this gem, pls get closer!!!", Toast.LENGTH_SHORT).show()
}
    else{ Toast.makeText(this, "U R ${distanceInMeters}m from this gem, pls get closer!!!", Toast.LENGTH_SHORT).show() }
return true
    }




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
        videoIntent.putExtra(
            "android.intent.extras.CAMERA_FACING",
            android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
        );
        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);

        /* Go to front facing camera, this is a hack.
         * Does not work on all models or versions of android.
         * A better solution would be to build own CameraActivity
         */
        videoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        videoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
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
                val riversRef = storageRef.child("${currentUser.email}/$currentLatLng.jpg")

                riversRef.putFile(file!!).addOnCompleteListener {
                    // Get a URL to the uploaded content
                    riversRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result.toString()

                            // Pin style


                            // Add object to firebase
                            database = FirebaseDatabase.getInstance()
                            databaseReference = database!!.reference!!.child("markers").push()
                            auth = FirebaseAuth.getInstance()

                            if (currentUser.email != null && currentLatLng != null && downloadUri != null) {
                                databaseReference.child("user").setValue(currentUser.email)
                                databaseReference.child("latLng").setValue(currentLatLng)
                                databaseReference.child("color").setValue("blue")
                                databaseReference.child("url").setValue(downloadUri)



                                val markerOptions = MarkerOptions().position(currentLatLng).title("${currentUser.email} $downloadUri" )
                                markerOptions.icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        BitmapFactory.decodeResource(resources, R.mipmap.gem_basic)
                                    )
                                )


                                // Add marker to map
                                map.addMarker(markerOptions)
                            }

                        }

                    }
                }

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
                    // Opens the camera front facing, this is a hack, does nto work on all versions of Android nor on all devices.
                    // A better solution would be to build a camera activity from scratch
                    val userForMarker = it.child("user").value
                    val latForMarker = it.child("latLng").child("latitude").value
                    val lngForMarker = it.child("latLng").child("longitude").value
                    val urlForMarker = it.child("url").value


                    if (userForMarker != null && latForMarker != null && lngForMarker != null && urlForMarker != null) {
                        val places = LatLng(latForMarker as Double, lngForMarker as Double)
                        val markerOptions = MarkerOptions().position(places).title("$userForMarker $urlForMarker"  as String? )
                        markerOptions.icon(
                            BitmapDescriptorFactory.fromBitmap(
                                BitmapFactory.decodeResource(resources, R.mipmap.gem_basic)
                            )
                        )
                        map.addMarker(markerOptions)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "listener:onCancelled", databaseError.toException())
            }
        })
    }


}
