package com.example.mastersproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity(), MyAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityArrayList: ArrayList<Item>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        activityArrayList = arrayListOf()
        myAdapter = MyAdapter(activityArrayList)
        myAdapter.setOnItemClickListener(this)

        setupRecyclerView()
        setupUI()
        eventChangeListener()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter
    }

    private fun setupUI() {
        val mapButton = findViewById<ImageView>(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, MapTest::class.java)
            intent.putParcelableArrayListExtra("activityArrayList", activityArrayList)
            startActivity(intent)
        }

        val profileButton = findViewById<ImageView>(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        val homeButton = findViewById<ImageView>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        val fab: FloatingActionButton = findViewById(R.id.floatingActionButton2)
        fab.setOnClickListener { view ->
            showPopupWindow(view)
        }
    }


    //Method to show filter menu
    private fun showPopupWindow(anchorView: View) {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        val popupView = layoutInflater.inflate(R.layout.floating_action_popup, rootView, false)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true  // Make popup focusable
        )


        popupView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Find the Filter buttons location
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)

        // Set x coordinate to place the menu against the edge of the screen
        val x = if (location[0] > resources.displayMetrics.widthPixels / 2) {
            resources.displayMetrics.widthPixels - popupView.measuredWidth
        } else {
            0
        }

        // Set y coordinate to align the bottom of the menu with the top of the FloatingActionButton
        val y = location[1] - popupView.measuredHeight

        // Show the menu at the calculated position
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)
    }

    //Method to start map activity and pass item name
    override fun onItemClick(name: String) {
        val intent = Intent(this, MapTest::class.java)
        intent.putExtra("selectedItemName", name)
        startActivity(intent)
    }

    //Method to check for changes in activity database
    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Activities1")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
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
                            activityArrayList.add(dc.document.toObject(Item::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })
    }
}

