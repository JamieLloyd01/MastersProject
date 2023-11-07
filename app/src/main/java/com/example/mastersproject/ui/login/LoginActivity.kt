package com.example.mastersproject.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.mastersproject.ForgottenPassword

import com.example.mastersproject.R
import com.example.mastersproject.SignUp
import com.example.mastersproject.HomeActivity
import com.example.mastersproject.MapTest
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val enteredEmail = findViewById<TextView>(R.id.email)
        val enteredPassword = findViewById<TextView>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val progressBar = findViewById<ProgressBar>(R.id.loading)

        login.setOnClickListener(){
            progressBar.visibility = View.VISIBLE
            val email = enteredEmail.text.toString()
            val password = enteredPassword.text.toString()


            if (email.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        }











        val navigateButton = findViewById<Button>(R.id.navigateToSignUp)
        navigateButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUp::class.java)
            startActivity(intent)
        }

        val forgottenPasswordButton = findViewById<Button>(R.id.forgot_password)
        forgottenPasswordButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgottenPassword::class.java)
            startActivity(intent)
        }

        //This is just a navigation button to use before you add in the login code
        val navToHome = findViewById<Button>(R.id.navigateToHome)
        navToHome.setOnClickListener {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        val navToMap = findViewById<Button>(R.id.mapTestButton)
        navToMap.setOnClickListener {
            val intent = Intent(this@LoginActivity, MapTest::class.java)
            startActivity(intent)
        }


    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}