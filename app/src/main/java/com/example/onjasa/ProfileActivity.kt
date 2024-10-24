package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AlertDialog // Pastikan ini diimpor
import android.content.DialogInterface
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btnlogout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi Button logout
        btnlogout = findViewById(R.id.btnlogout)

        btnlogout.setOnClickListener {
            // Buat AlertDialog untuk konfirmasi logout
            AlertDialog.Builder(this).apply {
                setTitle("Konfirmasi Logout")
                setMessage("Apakah Anda yakin ingin logout?")
                setPositiveButton("Ya") { dialog, _ ->
                    // Jika user memilih 'Ya', lakukan logout
                    auth.signOut()
                    Intent(this@ProfileActivity, LoginActivity::class.java).also { intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    dialog.dismiss()
                }
                setNegativeButton("Tidak") { dialog, _ ->
                    // Jika user memilih 'Tidak', tutup dialog
                    dialog.dismiss()
                }
                create()
                show()
            }
        }

        // Inisialisasi BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set listener untuk navigasi
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    true
                }
                R.id.activity -> {
                    // Pindah ke OrderListActivity
                    val intent = Intent(this@ProfileActivity, OrderListActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    true
                }
                R.id.chat -> {
                    // Handle Chat navigation (jika perlu)
                    true
                }
                R.id.navigation_profile -> {

                    true
                }
                else -> false
            }
        }
    }
}