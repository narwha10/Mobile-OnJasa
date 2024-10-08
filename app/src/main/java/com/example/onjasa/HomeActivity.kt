package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnlogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi Button logout
        btnlogout = findViewById(R.id.btnlogout)

        btnlogout.setOnClickListener {
            auth.signOut()
            Intent(this@HomeActivity, LoginActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        val notification: ImageView = findViewById(R.id.notification)

        notification.setOnClickListener {
            val intent = Intent(this@HomeActivity, NotificationActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set listener untuk navigasi
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle Home navigation (jika perlu)
                    true
                }
                R.id.activity -> {
                    // Pindah ke OrderListActivity
                    val intent = Intent(this, OrderListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    // Handle Chat navigation (jika perlu)
                    true
                }
                R.id.navigation_profile -> {
                    // Handle Profile navigation (jika perlu)
                    true
                }
                else -> false
            }
        }
    }
}
