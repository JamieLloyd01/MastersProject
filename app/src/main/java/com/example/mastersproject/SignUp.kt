package com.example.mastersproject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mastersproject.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class SignUp : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var activityArrayListUser: ArrayList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initializeUI()
        setupListeners()
    }

    private fun initializeUI() {
        activityArrayListUser = ArrayList()
        eventChangeListener()
    }

    private fun setupListeners() {
        val inputEmail = findViewById<TextView>(R.id.email)
        val inputUsername = findViewById<TextView>(R.id.username)
        val inputPassword = findViewById<TextView>(R.id.password)
        val createAccountButton = findViewById<Button>(R.id.signup)
        val progressBar = findViewById<ProgressBar>(R.id.loading)
        val accountAlreadyButton = findViewById<Button>(R.id.already_signed_up)

        createAccountButton.setOnClickListener {
            handleAccountCreation(inputEmail, inputUsername, inputPassword, progressBar)
        }

        accountAlreadyButton.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    private fun handleAccountCreation(
        inputEmail: TextView,
        inputUsername: TextView,
        inputPassword: TextView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE
        val email = inputEmail.text.toString()
        val username = inputUsername.text.toString()
        val password = inputPassword.text.toString()

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        createUserAccount(email, password, username, progressBar)
    }

    private fun createUserAccount(
        email: String,
        password: String,
        username: String,
        progressBar: ProgressBar
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    processSuccessfulAccountCreation(username, progressBar)
                } else {
                    processFailedAccountCreation(task.exception, progressBar)
                }
            }
    }

    private fun processSuccessfulAccountCreation(
        username: String,
        progressBar: ProgressBar
    ) {
        val uid = auth.currentUser?.uid ?: ""
        updateFirestoreUsername(uid, username)

        activityArrayListUser.forEach { item ->
            updateFirestoreCompletion(uid, item.name)
        }
        createCompletionNumberFieldForUser(uid)

        progressBar.visibility = View.GONE
        Toast.makeText(baseContext, "Account created", Toast.LENGTH_SHORT).show()
    }

    private fun processFailedAccountCreation(
        exception: Exception?,
        progressBar: ProgressBar
    ) {
        Log.w(TAG, "createUserWithEmail:failure", exception)
        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    private fun createCompletionNumberFieldForUser(uid: String) {
        val fieldName = "completionNumber"
        val initialValue = 0
        val userIntFieldMap = hashMapOf(fieldName to initialValue)
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(userIntFieldMap, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Integer field $fieldName created with initial value $initialValue")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error creating integer field $fieldName", e)
            }
    }


    private fun updateFirestoreCompletion(uid: String, completionBoolName: String?) {
        completionBoolName?.let { fieldName ->
            val userCompletionUpdate = hashMapOf(fieldName to false)

            db.collection("users").document(uid)
                .set(userCompletionUpdate, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Completion status updated successfully for $fieldName")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating completion status for $fieldName", e)
                }
        }
    }


    private fun updateFirestoreUsername(uid: String, username: String) {
        val userMap = hashMapOf("username" to username,  "profilePicURL" to "")
        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

    private fun eventChangeListener() {

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