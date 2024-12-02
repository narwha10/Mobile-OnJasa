package com.example.onjasa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmNewPasswordEditText: EditText
    private lateinit var btnConfirm: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        // Menghubungkan EditText dan Button dari XML ke kode
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText)
        btnConfirm = findViewById(R.id.btnConfirm)

        // Menambahkan click listener pada tombol Confirm
        btnConfirm.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmNewPasswordEditText.text.toString().trim()

            // Validasi input password
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Mohon isi kedua kolom password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil username dari intent
            val username = intent.getStringExtra("USERNAME")
            if (username != null) {
                updatePassword(username, newPassword)
            } else {
                Toast.makeText(this, "Username tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan password ke Firestore
    private fun updatePassword(username: String, newPassword: String) {
        firestore.collection("users")
            .whereEqualTo("username", username) // Cari pengguna berdasarkan username
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Ambil dokumen pertama yang ditemukan
                    val document = documents.documents[0]
                    val userRef = firestore.collection("users").document(document.id)

                    // Update password
                    userRef.update("password", newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Password berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                            finish() // Menutup activity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal memperbarui password: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Username tidak valid.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
