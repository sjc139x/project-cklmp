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
import android.util.Log
import android.widget.ImageButton
import com.google.android.gms.maps.model.*
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.view.MenuItem

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var changeView: Switch
    private val VIDEO_REQUEST = 101
    private var mStorageRef: StorageReference? = null
    private val storage = FirebaseStorage.getInstance()
    private var storageRef = storage.getReference()
    private lateinit var currentUser: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var gemColor: String = "blue"
    private lateinit var blueButton: ImageButton
    private lateinit var purpleButton: ImageButton
    private lateinit var redButton: ImageButton
    private lateinit var placeMarkerGem: ImageButton
    private lateinit var storeSnapshot: DataSnapshot
    private lateinit var drawerLayout: DrawerLayout

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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()

        setUpMenuDrawer()
    }

    private fun setUpMenuDrawer() {
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(null)
        }

        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            // navigation item clicks
            when (menuItem.itemId) {

                R.id.nav_social_friend_list -> {
                    finish()
                    startActivity(Intent(this, FriendsActivity::class.java))
                }

                R.id.nav_account_log_out -> {
                    auth.signOut()
                    finish()
                    startActivity(Intent(this, LoginActivity::class.java))
                }

                R.id.nav_account_reset_password -> {
                    sendPasswordResetEmail()
                }

            }

            true

        }
        // Add code here to update the UI based on the item selected

    }

    private fun sendPasswordResetEmail() {
        val userEmail = auth.currentUser!!.email
        auth.sendPasswordResetEmail(userEmail!!)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)
        changeView = findViewById(R.id.change_detail)
        blueButton = findViewById(R.id.blue_button)
        purpleButton = findViewById(R.id.purple_button)
        redButton = findViewById(R.id.red_button)
        placeMarkerGem = findViewById(R.id.place_marker_gem)

        // Places a marker on the map on the click of a button, centres camera, no zooming
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            placeMarkerGem.setOnClickListener {
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

        blueButton.setOnClickListener {
            changeGemColour("blue",googleMap)
        }
        purpleButton.setOnClickListener {
            changeGemColour("purple",googleMap)
        }
        redButton.setOnClickListener {
            changeGemColour("red",googleMap)
        }

        setUpMap()

        // Get markers from firebase
        getMarkers(googleMap)
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
                                databaseReference.child("color").setValue(gemColor)
                                databaseReference.child("url").setValue(downloadUri)

                                val markerOptions = MarkerOptions().position(currentLatLng).title("${currentUser.email} $downloadUri" )
                                markerOptions.icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        when (gemColor) {
                                            "blue" -> BitmapFactory.decodeResource(resources, R.drawable.gem_blue)
                                            "purple" -> BitmapFactory.decodeResource(resources, R.drawable.gem_purple)
                                            "red" -> BitmapFactory.decodeResource(resources, R.drawable.gem_red)
                                            else -> BitmapFactory.decodeResource(resources, R.drawable.gem_blue)
                                        }
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


    private fun getMarkers(googleMap: GoogleMap) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
        databaseReference.child("markers").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                storeSnapshot = snapshot
                loadMarkers(snapshot, googleMap)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "listener:onCancelled", databaseError.toException())
            }
        })
    }

     fun loadMarkers(snapshot: DataSnapshot, googleMap: GoogleMap) {
         map = googleMap
         map.clear()
         val markers = snapshot.children
         markers.forEach {
             val userForMarker = it.child("user").value
             val latForMarker = it.child("latLng").child("latitude").value
             val lngForMarker = it.child("latLng").child("longitude").value
             val urlForMarker = it.child("url").value
             val colorForMarker = it.child("color").value

             if (userForMarker != null && latForMarker != null && lngForMarker != null && urlForMarker != null && colorForMarker == gemColor) {
                 val places = LatLng(latForMarker as Double, lngForMarker as Double)
                 val markerOptions = MarkerOptions().position(places).title("$userForMarker $urlForMarker"  as String? )
                 markerOptions.icon(
                     BitmapDescriptorFactory.fromBitmap(
                         when (colorForMarker) {
                             "blue" -> BitmapFactory.decodeResource(resources, R.drawable.gem_blue)
                             "purple" -> BitmapFactory.decodeResource(resources, R.drawable.gem_purple)
                             "red" -> BitmapFactory.decodeResource(resources, R.drawable.gem_red)
                             else -> BitmapFactory.decodeResource(resources, R.drawable.gem_blue)
                         }
                     )
                 )
                 map.addMarker(markerOptions)
             }
         }
     }

    fun changeGemColour(colour: String, googleMap: GoogleMap) {
        gemColor = colour
        placeMarkerGem = findViewById(R.id.place_marker_gem)
        when (colour) {
            "blue" -> placeMarkerGem.setImageResource(R.drawable.gem_blue)
            "purple" -> placeMarkerGem.setImageResource(R.drawable.gem_purple)
            "red" -> placeMarkerGem.setImageResource(R.drawable.gem_red)
            else -> placeMarkerGem.setImageResource(R.drawable.gem_blue)
        }
        loadMarkers(storeSnapshot, googleMap)
    }
}
