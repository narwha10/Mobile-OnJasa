package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnLogin: Button
    private lateinit var etUsername: EditText  // Mengubah dari etEmail ke etUsername
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi view dari layout
        btnLogin = findViewById(R.id.btnlogin)
        etUsername = findViewById(R.id.etUsername)  // Menggunakan EditText untuk username
        etPassword = findViewById(R.id.etPassword)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()  // Ambil username
            val password = etPassword.text.toString().trim()

            // Validasi input
            if (username.isEmpty()) {
                etUsername.error = "Username harus diisi"
                etUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                etPassword.error = "Password harus lebih dari 6 karakter"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Proses login dengan memeriksa data di Firestore
            loginUser(username, password)
        }

        // Inisialisasi TextView untuk berpindah ke SignUpActivity
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        val forgotPasswordButton = findViewById<TextView>(R.id.forgotPasswordButton)
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        // Cari pengguna berdasarkan username dan password di Firestore
        firestore.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val level = document.getBoolean("level") ?: false  // Ambil nilai level, default false jika null

                    val targetActivity = if (level) HomeTechActivity::class.java else HomeActivity::class.java
                    val intent = Intent(this@LoginActivity, targetActivity).apply {
                        putExtra("username", username)  // Kirim username sebagai extra
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                } else {
                    // Jika tidak ditemukan, tampilkan pesan kesalahan
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStart() {
        super.onStart()
        // Di sini tidak ada autentikasi otomatis, jadi abaikan pengecekan FirebaseAuth
    }
}
