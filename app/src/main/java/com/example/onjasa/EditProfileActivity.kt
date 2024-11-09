package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etAlamat: EditText
    private lateinit var etNohp: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView // Tambahkan btnBack
    private lateinit var db: FirebaseFirestore
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Inisialisasi FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        // Menginisialisasi View
        etAlamat = findViewById(R.id.etAlamat)
        etNohp = findViewById(R.id.etNohp)
        btnSave = findViewById(R.id.btnsave)
        btnBack = findViewById(R.id.imageViewBack) // Inisialisasi btnBack

        // Menerapkan insets untuk layout utama
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil username dari intent yang dikirim dari ProfileActivity
        username = intent.getStringExtra("USERNAME")

        // Memuat data user dari Firestore
        loadUserData()

        // Set onClickListener untuk tombol simpan
        btnSave.setOnClickListener {
            updateUserProfile()
        }

        // Set onClickListener untuk tombol kembali dengan konfirmasi
        btnBack.setOnClickListener {
            showBackConfirmationDialog()
        }
    }

    // Fungsi untuk menampilkan dialog konfirmasi saat tombol kembali ditekan
    private fun showBackConfirmationDialog() {
        // Buat dan tampilkan dialog konfirmasi
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin membatalkan perubahan dan kembali?")
            .setPositiveButton("Ya") { _, _ ->
                // Kembali ke ProfileActivity
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USERNAME", username) // Kirim kembali username jika diperlukan
                startActivity(intent)
                finish() // Tutup EditProfileActivity
            }
            .setNegativeButton("Tidak", null) // Tutup dialog jika memilih "Tidak"
            .show()
    }

    // Fungsi untuk mengambil data pengguna berdasarkan username
    private fun loadUserData() {
        username?.let { uname ->
            db.collection("users").whereEqualTo("username", uname)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val alamat = document.getString("alamat")
                        val nohp = document.getString("nohp")

                        // Menampilkan data di EditText
                        etAlamat.setText(alamat ?: "")
                        etNohp.setText(nohp ?: "")
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "Username tidak ditemukan.", Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk memperbarui data alamat dan nohp pengguna
    private fun updateUserProfile() {
        val newAlamat = etAlamat.text.toString()
        val newNohp = etNohp.text.toString()

        // Validasi data input
        if (newAlamat.isEmpty() || newNohp.isEmpty()) {
            Toast.makeText(this, "Alamat dan Nomor HP harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        username?.let { uname ->
            db.collection("users").whereEqualTo("username", uname)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val userId = document.id

                        // Update alamat dan nohp di Firestore
                        val userUpdates = mapOf(
                            "alamat" to newAlamat,
                            "nohp" to newNohp
                        )
                        db.collection("users").document(userId)
                            .update(userUpdates)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profil berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                                finish() // Menutup EditProfileActivity setelah berhasil update
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal memperbarui profil: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
