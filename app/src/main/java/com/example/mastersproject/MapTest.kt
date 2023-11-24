package com.example.mastersproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mastersproject.databinding.ActivityMapTestBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class MapTest : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapTestBinding
    private lateinit var activityArrayListMap: ArrayList<Item>
    private lateinit var db: FirebaseFirestore
    private var itemName: String? = null
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityArrayListMap = ArrayList()
        eventChangeListener()

        binding = ActivityMapTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        itemName = intent.getStringExtra("selectedItemName")

        setupUI()

    }

    private fun setupUI() {
        findViewById<ImageView>(R.id.homeButton).setOnClickListener {
            navigateTo(HomeActivity::class.java)
        }

        findViewById<ImageView>(R.id.profileButton).setOnClickListener {
            navigateTo(ProfileActivity::class.java)
        }

        val fab2: FloatingActionButton = findViewById(R.id.floatingActionButton3)
        fab2.setOnClickListener { view ->
            showPopupWindow(view)
        }
    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    private fun showPopupWindow(anchorView: View) {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dummyParent = FrameLayout(this) // Or any other ViewGroup
        val popupView = layoutInflater.inflate(R.layout.floating_action_popup, dummyParent, false)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true  // Make popup focusable
        )

        // Measure the size of the popup so it can be positioned
        popupView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Find location of the Filters button on the screen
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)

        // Set x coordinate against the edge of the screen
        val x = if (location[0] > resources.displayMetrics.widthPixels / 2) {
            resources.displayMetrics.widthPixels - popupView.measuredWidth
        } else {
            0
        }

        // Set y coordinate to align the bottom of the popup with the top of the Filter Button
        val y = location[1] - popupView.measuredHeight

        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
    }

    private fun hideDetailView() {
        val mapDetailContainer = findViewById<FrameLayout>(R.id.mapDetailContainer)
        mapDetailContainer.visibility = View.GONE
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Set Swansea to be default entry point
        val defaultEntry = LatLng(51.7186366707045, -3.7593181317647058)
        val cameraPosition = CameraPosition.Builder()
            .target(defaultEntry)
            .zoom(8.5f)          // Zoom level so that all markers start visible
            .build()

        // Move the camera
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        // Close card view by clicking anywhere on map
        mMap.setOnMapClickListener {
            hideDetailView()
        }

        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

        // if card clicked in list view show that card and marker on map
        if(itemName != null){
            for (item in activityArrayListMap) {
                if (item.name == itemName){
                        val geoPointFromCard = item.location
                        if (geoPointFromCard != null) {
                        val cardLatLng = LatLng(geoPointFromCard.latitude, geoPointFromCard.longitude)

                        val cameraPositionCard = CameraPosition.Builder()
                            .target(cardLatLng)  // Set the marker's position as the camera target
                            .zoom(16.0f)
                            .build()

                        // Move the camera to the new position
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionCard))

                            // Create a bundle to pass the selected item to the MapDetailView fragment
                            val bundle = Bundle()
                            bundle.putParcelable("selectedItem", item)

                            // Create the MapDetailView fragment
                            val mapDetailViewFragment = MapDetailView()
                            mapDetailViewFragment.arguments = bundle

                            val mapDetailContainer = findViewById<FrameLayout>(R.id.mapDetailContainer)

                            // Clear any existing fragments in the container
                            supportFragmentManager.beginTransaction()
                                .replace(mapDetailContainer.id, mapDetailViewFragment)
                                .commit()

                            mapDetailContainer.visibility = View.VISIBLE

                            val translationY = 1035f
                            mapDetailContainer.translationY = translationY
                        }

                }
            }
        }

        for (item in activityArrayListMap) {
            val geoPoint = item.location

            userId?.let { uid ->
                val userDoc = db.collection("users").document(uid)
                userDoc.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val completeCheck = item.name?.let { it1 -> document.getBoolean(it1) } ?: false
                        if(!completeCheck){
                            val customMarker: BitmapDescriptor = when (item.filter1) {
                                "Sports & Exercise" -> getCustomMarkerRed()
                                "Games" -> getCustomMarkerPurple()
                                "Outdoors" -> getCustomMarkerSkyBlue()
                                "Nature & Wildlife" -> getCustomMarkerGreen()
                                "Historic" -> getCustomMarkerYellow()
                                "Arts" -> getCustomMarkerBlue()
                                else -> getCustomMarkerBlack()
                            }

                            // Check if geoPoint is not null
                            if (geoPoint != null) {
                                val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                                // Add a marker for each uncompleted GeoPoint
                                mMap.addMarker(
                                    MarkerOptions().position(latLng).title(item.name).icon(customMarker)
                                )
                            }
                        }
                        else{
                            val customMarker: BitmapDescriptor = when (item.filter1) {
                                "Sports & Exercise" -> getCustomMarkerRedGreen()
                                "Games" -> getCustomMarkerPurpleGreen()
                                "Outdoors" -> getCustomMarkerSkyBlueGreen()
                                "Nature & Wildlife" -> getCustomMarkerGreenGreen()
                                "Historic" -> getCustomMarkerYellowGreen()
                                "Arts" -> getCustomMarkerBlueGreen()
                                else -> getCustomMarkerBlack()
                            }

                            // Check if geoPoint is not null
                            if (geoPoint != null) {
                                val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                                // Add a marker for each completed GeoPoint
                                mMap.addMarker(
                                    MarkerOptions().position(latLng).title(item.name).icon(customMarker)
                                )
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "error loading user data", exception)
                }
            }
        }

        //Set card to appear when map marker clicked/tapped
        mMap.setOnMarkerClickListener { marker ->
            val selectedItem = activityArrayListMap.find { it.name == marker.title }
            if (selectedItem != null) {
                // Calculate the new camera position
                val markerLatLng = marker.position
                val cameraPosition1 = CameraPosition.Builder()
                    .target(markerLatLng)  // Set the marker's position as the camera target
                    .zoom(16.0f)
                    .build()

                // Move the camera to the new position
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1))

                // Create a bundle to pass the selected item to the MapDetailView fragment
                val bundle = Bundle()
                bundle.putParcelable("selectedItem", selectedItem)

                // Create the MapDetailView fragment
                val mapDetailViewFragment = MapDetailView()
                mapDetailViewFragment.arguments = bundle

                // Find the mapDetailContainer FrameLayout
                val mapDetailContainer = findViewById<FrameLayout>(R.id.mapDetailContainer)

                // Clear any existing fragments in the container
                supportFragmentManager.beginTransaction()
                    .replace(mapDetailContainer.id, mapDetailViewFragment)
                    .commit()

                mapDetailContainer.visibility = View.VISIBLE

                val translationY = 1035f // Change this value as needed
                mapDetailContainer.translationY = translationY
            }

            true
        }
    }

    private fun getCustomMarkerRed(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.RED
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)


        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCustomMarkerRedGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.RED
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.GREEN
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerPurple(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#800080")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCustomMarkerPurpleGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#800080")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.GREEN
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerSkyBlue(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.argb(255, 135, 206, 250)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCustomMarkerSkyBlueGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.argb(255, 135, 206, 250)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.GREEN
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



    private fun getCustomMarkerGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.argb(255, 54, 130, 64)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCustomMarkerGreenGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.argb(255, 54, 130, 64)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.GREEN
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerYellow(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#efc425")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCustomMarkerYellowGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#efc425")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.GREEN
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerBlue(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#00CED1")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerBlueGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#00CED1")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.GREEN
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCustomMarkerBlack(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val outlinePaint = Paint()
        outlinePaint.color = Color.BLACK
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun eventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("Activities1").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {

                    Log.e("Firestore Error", error.message.toString())
                    return

                }

                for (dc: DocumentChange in value?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {

                        activityArrayListMap.add(dc.document.toObject(Item::class.java))

                    }
                }
            }
        })
    }
}