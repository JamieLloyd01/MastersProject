package com.example.mastersproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.example.mastersproject.MyAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.integrity.internal.x


class HomeActivity : AppCompatActivity(), MyAdapter.OnItemClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var activityArrayList: ArrayList<Item>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore
    private val selectedFilters = mutableListOf<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)



        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        activityArrayList = arrayListOf()

        myAdapter = MyAdapter(activityArrayList)
        myAdapter.setOnItemClickListener(this)

        recyclerView.adapter = myAdapter

        EventChangeListener()


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

    private fun showPopupWindow(anchorView: View) {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = layoutInflater.inflate(R.layout.floating_action_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true  // Make popup focusable
        )

        // Measure the size of the popup to position it correctly
        popupView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Find location of the FloatingActionButton on the screen
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)

        // Set x coordinate to place the popup against the edge of the screen
        val x = if (location[0] > resources.displayMetrics.widthPixels / 2) {
            resources.displayMetrics.widthPixels - popupView.measuredWidth
        } else {
            0
        }

        // Set y coordinate to align the bottom of the popup with the top of the FloatingActionButton
        val y = location[1] - popupView.measuredHeight

        // Show the popup window at the calculated position
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y)


    }






    // Implement the onItemClick method from the interface
    override fun onItemClick(name: String) {
        // When an item is clicked, start the MapTest activity
        val intent = Intent(this, MapTest::class.java)
        // Pass the item name to MapTest using extras
        intent.putExtra("selectedItemName", name)
        startActivity(intent)
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

                                    activityArrayList.add(dc.document.toObject(Item::class.java))

                                }
                            }

                            myAdapter.notifyDataSetChanged()
                        }


                    })
    }
}
