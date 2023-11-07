package com.example.mastersproject

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

class SignUp : AppCompatActivity() {


    // Initialize Firebase Auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var activityArrayListUser: ArrayList<Item>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        activityArrayListUser = ArrayList()
        EventChangeListener()



        val inputEmail = findViewById<TextView>(R.id.email)
        val inputUsername = findViewById<TextView>(R.id.username)
        val inputPassword = findViewById<TextView>(R.id.password)
        val createAccount = findViewById<Button>(R.id.signup)
        val progressBar = findViewById<ProgressBar>(R.id.loading)


        createAccount.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = inputEmail.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()



            if (email.isEmpty()) {
                Toast.makeText(this@SignUp, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                Toast.makeText(this@SignUp, "Enter username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this@SignUp, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid ?: ""
                            updateFirestoreUsername(uid, username)

                            for (item in activityArrayListUser){
                                val completionBoolName = item.name
                                updateFirestoreCompletion(uid, completionBoolName)
                            }

                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                baseContext,
                                "Account created",
                                Toast.LENGTH_SHORT,
                            ).show()

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            progressBar.visibility = View.GONE

                        }
                    }


        }






        val accountAlready = findViewById<Button>(R.id.already_signed_up)
        accountAlready.setOnClickListener {
            val intent = Intent(this@SignUp, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateFirestoreCompletion(uid: String, completionBoolName: String?) {
        completionBoolName?.let {
            val userCompletionUpdate = hashMapOf(it to false)

            db.collection("users").document(uid)
                .set(userCompletionUpdate, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Completion status updated successfully for $it")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating completion status for $it", e)
                }
        }
    }

    private fun updateFirestoreUsername(uid: String, username: String) {
        val userMap = hashMapOf("username" to username)
        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

    private fun EventChangeListener() {

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

                        activityArrayListUser.add(dc.document.toObject(Item::class.java))

                    }
                }
            }


        })
    }
}