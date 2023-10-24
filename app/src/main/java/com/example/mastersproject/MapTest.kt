package com.example.mastersproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import android.view.View
import android.widget.RelativeLayout


class MapTest : AppCompatActivity(), OnMapReadyCallback, MyAdapter.OnItemClickListener  {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapTestBinding
    private lateinit var activityArrayListMap: ArrayList<Item>
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityArrayListMap = ArrayList()
        EventChangeListener()




        binding = ActivityMapTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val profileButton = findViewById<ImageView>(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this@MapTest, ProfileActivity::class.java)
            startActivity(intent)
        }


        val homeButton = findViewById<ImageView>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this@MapTest, HomeActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onItemClick(item: Item) {
        // Retrieve the selected item based on the marker's title
        val selectedItem = activityArrayListMap.find { it.name ==item.name }

        val geoPointToCenter = selectedItem?.location // Assuming this is your GeoPoint

        if (geoPointToCenter != null) {
            val centerLatLng = LatLng(geoPointToCenter.latitude, geoPointToCenter.longitude)

            // Calculate the new camera position to center on the LatLng
            val cameraPosition = CameraPosition.Builder()
                .target(centerLatLng)
                .zoom(16.0f)
                .bearing(0f)
                .tilt(0f)
                .build()

            // Move the map camera to the new camera position
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            // Move the map camera to the new camera position
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

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

            // Set the mapDetailContainer's visibility to visible
            mapDetailContainer.visibility = View.VISIBLE

            // Move the mapDetailContainer down by 300dp
            val translationY = 1035f // Change this value as needed
            mapDetailContainer.translationY = translationY
        }

        // Return true to indicate that the marker click event has been handled
        true
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val defaultEntry = LatLng(51.62143727059579, -3.9436196594046407)
        // Set the camera position with a specific location and zoom level
        val cameraPosition = CameraPosition.Builder()
            .target(defaultEntry) // The target location (LatLng) for the camera
            .zoom(12.0f)          // Zoom level (adjust as needed)
            .build()

// Move the camera to the specified position
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))



        // Iterate through the list of GeoPoints (assuming your GeoPoints are in the 'activityArrayList')
        for (item in activityArrayListMap) {
            val geoPoint = item.location

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

                // Add a marker for each GeoPoint

                mMap.addMarker(
                    MarkerOptions().position(latLng).title(item.name).icon(customMarker)
                    )
            }
        }


        // In your `onMapReady` method
        mMap.setOnMarkerClickListener { marker ->
            // Retrieve the selected item based on the marker's title
            val selectedItem = activityArrayListMap.find { it.name == marker.title }

            if (selectedItem != null) {
                // Calculate the new camera position to center the marker
                val markerLatLng = marker.position
                val cameraPosition = CameraPosition.Builder()
                    .target(markerLatLng)  // Set the marker's position as the camera target
                    .zoom(16.0f)  // Retain the current zoom level
                    .bearing(0f)  // Optional: Set the desired bearing (0 means north)
                    .tilt(0f)  // Optional: Set the desired tilt
                    .build()

                // Move the map camera to the new camera position
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

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

                // Set the mapDetailContainer's visibility to visible
                mapDetailContainer.visibility = View.VISIBLE

                // Move the mapDetailContainer down by 300dp
                val translationY = 1035f // Change this value as needed
                mapDetailContainer.translationY = translationY
            }

            // Return true to indicate that the marker click event has been handled
            true
        }




    }


    private fun getCustomMarkerRed(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.RED
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerPurple(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#800080")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerSkyBlue(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.argb(255, 135, 206, 250)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



    private fun getCustomMarkerGreen(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.argb(255, 54, 130, 64)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerYellow(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#efc425")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerBlue(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.parseColor("#00CED1")
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun getCustomMarkerBlack(): BitmapDescriptor {
        val width = 85 // Width of the marker icon
        val height = 85 // Height of the marker icon
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw a white circle as the outline
        val outlinePaint = Paint()
        outlinePaint.color = Color.BLACK
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, outlinePaint)

        // Draw a green circle in the center
        val centerPaint = Paint()
        centerPaint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width / 4.5f, centerPaint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun EventChangeListener() {

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