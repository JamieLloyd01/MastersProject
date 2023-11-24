package com.example.mastersproject

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupUI()
    }

    private fun setupUI() {
        findViewById<ImageView>(R.id.homeButton).setOnClickListener {
            navigateTo(HomeActivity::class.java)
        }

        findViewById<ImageView>(R.id.mapButton).setOnClickListener {
            navigateTo(MapTest::class.java)
        }

        findViewById<ImageView>(R.id.logout).setOnClickListener {
            auth.signOut()
            navigateTo(LoginActivity::class.java)
            finish()
        }

        val user = auth.currentUser
        user?.let {
            val uid = it.uid
            loadUserProfilePicture(uid)
            loadUserData(uid)
        }

        findViewById<ImageView>(R.id.newProfilePic).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    private fun loadUserProfilePicture(uid: String) {
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val profilePicUrl = document.getString("profilePicURL")
                    if (!profilePicUrl.isNullOrEmpty()) {
                        Picasso.get().load(profilePicUrl).into(profileImage)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error fetching profile picture: ", exception)
            }
    }

    private fun loadUserData(uid: String) {
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                // Extracting the username from firebase doc and setting it to the TextView
                val username = document.getString("username")
                val usernameTextView = findViewById<TextView>(R.id.username)
                usernameTextView.text = username

                // Extracting the completion number from firebase doc and setting it to the TextView
                val completionNumber = document.get("completionNumber")?.toString()
                val completionNumberTextView = findViewById<TextView>(R.id.completedNumber)
                completionNumberTextView.text = completionNumber ?: "0"  // Default to "0" if null
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "error loading user data", exception)
        }
    }

    private fun handleImage(uri: Uri) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = it.uid
            uploadImageToFirebase(uri, uid)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri, uid: String) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = FirebaseStorage.getInstance().getReference("profileImages/$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImage = findViewById<ImageView>(R.id.profileImage)
                    Picasso.get().load(uri).into(profileImage)
                    updateUserProfilePicUrl(uid, uri.toString())
                }
            }
    }

    private fun updateUserProfilePicUrl(uid: String, profilePicUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)

        userRef.update("profilePicURL", profilePicUrl)
            .addOnSuccessListener {
                Log.d(TAG, "User profile picture URL updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error updating user profile picture URL", e)
            }
    }
}
