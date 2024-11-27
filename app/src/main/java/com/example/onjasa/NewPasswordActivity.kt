package com.example.onjasa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmNewPasswordEditText: EditText
    private lateinit var btnConfirm: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        // Inisialisasi Firebase Auth dan Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Menghubungkan EditText dan Button dari XML ke kode
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText)
        btnConfirm = findViewById(R.id.btnConfirm)

        // Menambahkan click listener pada tombol Confirm
        btnConfirm.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmNewPasswordEditText.text.toString()

            // Validasi input password
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in both password fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update password ke Firestore
            updatePassword(newPassword)
        }
    }

    // Fungsi untuk menyimpan password ke Firestore
    private fun updatePassword(newPassword: String) {
        // Menyimpan password baru ke Firestore
        val user = auth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)
            userRef.update("password", newPassword)
                .addOnSuccessListener {
                    Toast.makeText(this, "Password successfully updated.", Toast.LENGTH_SHORT).show()
                    // Mengarahkan user ke activity lain atau home screen
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating password: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}