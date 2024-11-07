package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var btnlogout: Button
    private lateinit var filledButton: Button
    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Menerapkan insets untuk layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi FirebaseAuth dan FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inisialisasi Button logout dan filledButton
        btnlogout = findViewById(R.id.btnlogout)
        filledButton = findViewById(R.id.filledButton)

        // Inisialisasi TextView untuk username
        usernameTextView = findViewById(R.id.textView3)

        // Ambil data username dari Intent dan tampilkan di TextView
        val username = intent.getStringExtra("USERNAME")
        usernameTextView.text = username ?: "Username tidak tersedia"

        // Set OnClickListener untuk button filledButton
        filledButton.setOnClickListener {
            if (username != null) {
                updateStatusToTechnician(username)
            } else {
                Toast.makeText(this, "Username tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
        }

        btnlogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Inisialisasi BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set listener untuk navigasi
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    true
                }
                R.id.activity -> {
                    val intent = Intent(this@ProfileActivity, OrderListActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this@ProfileActivity, MapViewActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun updateStatusToTechnician(username: String) {
        // Cari dokumen pada koleksi "users" di Firestore di mana field username cocok
        db.collection("users").whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Ambil dokumen pertama yang cocok
                    val document = documents.documents[0]
                    val userId = document.id // Dapatkan ID dokumen untuk referensi

                    // Update field "status" menjadi true
                    db.collection("users").document(userId)
                        .update("level", true)
                        .addOnSuccessListener {
                            // Tampilkan Toast jika berhasil
                            Toast.makeText(this, "You are a technician now!", Toast.LENGTH_SHORT).show()

                            // Lakukan logout setelah update berhasil
                            auth.signOut()

                            // Intent ke LoginActivity
                            Intent(this@ProfileActivity, LoginActivity::class.java).also { intent ->
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { e ->
                            // Tampilkan pesan jika gagal memperbarui status
                            Toast.makeText(this, "Gagal mengupdate status: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Jika tidak ada dokumen yang cocok, tampilkan pesan
                    Toast.makeText(this, "Username tidak ditemukan di database.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Konfirmasi Logout")
            setMessage("Apakah Anda yakin ingin logout?")
            setPositiveButton("Ya") { dialog, _ ->
                auth.signOut()
                Intent(this@ProfileActivity, LoginActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                dialog.dismiss()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
