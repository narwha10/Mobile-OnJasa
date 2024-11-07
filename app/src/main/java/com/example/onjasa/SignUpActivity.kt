package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnregister: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etUsername: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNohp: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Initialize UI components
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        btnregister = findViewById(R.id.btnregister)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etUsername = findViewById(R.id.etUsername)
        etAlamat = findViewById(R.id.etAlamat)
        etNohp = findViewById(R.id.etNohp)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Navigate to login screen on "Sign In" click
        tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Register button click handler
        btnregister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val nohp = etNohp.text.toString().trim()

            // Input validation
            if (username.isEmpty()) {
                etUsername.error = "Username harus diisi"
                etUsername.requestFocus()
                return@setOnClickListener
            }
            if (alamat.isEmpty()) {
                etAlamat.error = "Alamat harus diisi"
                etAlamat.requestFocus()
                return@setOnClickListener
            }
            if (nohp.isEmpty() || nohp.length != 12 || !nohp.matches(Regex("\\d+"))) {
                etNohp.error = "Nomor HP harus berisi angka saja dan 12 digit"
                etNohp.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email tidak valid"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                etPassword.error = "Password harus lebih dari 6 karakter"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Save user data to Firestore
            saveUserToDatabase(username, alamat, nohp, email, password)
        }

        // Set up edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to save user to Firestore
    private fun saveUserToDatabase(username: String, alamat: String, nohp: String, email: String, password: String) {
        val userId = firestore.collection("users").document().id
        val user = hashMapOf(
            "username" to username,
            "alamat" to alamat,
            "nohp" to nohp,
            "email" to email,
            "password" to password,
            "level" to false // Default level set to false
        )

        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                // Successfully saved, proceed to GetStartedActivity
                Intent(this@SignUpActivity, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
            .addOnFailureListener { e ->
                // Failed to save data
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
