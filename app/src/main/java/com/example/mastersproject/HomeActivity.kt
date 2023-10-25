package com.example.mastersproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.example.mastersproject.MyAdapter



class HomeActivity : AppCompatActivity(), MyAdapter.OnItemClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var activityArrayList: ArrayList<Item>
    private lateinit var myAdapter: MyAdapter
    private lateinit var db: FirebaseFirestore


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
