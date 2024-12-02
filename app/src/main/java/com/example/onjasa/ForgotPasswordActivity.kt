package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var btnConfirm: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Inisialisasi
        emailEditText = findViewById(R.id.emailEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        btnConfirm = findViewById(R.id.btnConfirm)
        firestore = FirebaseFirestore.getInstance()

        btnConfirm.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Masukkan email yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi ke Firestore
            validateUser(email, username)
        }
    }

    private fun validateUser(email: String, username: String) {
        firestore.collection("users") // Pastikan koleksi Firestore Anda adalah "users"
            .whereEqualTo("email", email)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Jika valid, tampilkan toast dan pindah ke NewPasswordActivity
                    Toast.makeText(this, "Silakan ganti password Anda", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, NewPasswordActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Email atau username tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
