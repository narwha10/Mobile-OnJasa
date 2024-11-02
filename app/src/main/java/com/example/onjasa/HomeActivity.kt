package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class HomeActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        FirebaseApp.initializeApp(this)

        // Menemukan elemen UI
        usernameTextView = findViewById(R.id.username_text)
        profilePicture = findViewById(R.id.profile_picture)

        // Ambil username dari Intent
        val username = intent.getStringExtra("username") ?: "Guest"

        // Show Toast to confirm the username is received
        Log.d("HomeActivity", "Received username: $username")
        Toast.makeText(this, "Welcome, $username", Toast.LENGTH_SHORT).show()

        usernameTextView = findViewById(R.id.username_text)
        profilePicture = findViewById(R.id.profile_picture)

        // Tampilkan username
        usernameTextView.text = "Hello, $username"

        // Set listener untuk notifikasi
        val notification: ImageView = findViewById(R.id.notification)
        notification.setOnClickListener {
            val intent = Intent(this@HomeActivity, NotificationActivity::class.java)
            startActivity(intent)
        }

        // AC Repair Handler
        val acRepair: CardView = findViewById(R.id.ac_repair_card)
        acRepair.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Repair")
            intent.putExtra("username", username)  // Kirim username
            startActivity(intent)
        }

        // AC Installation Handler
        val acInstallation: CardView = findViewById(R.id.ac_installation_card)
        acInstallation.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Installation")
            intent.putExtra("username", username)  // Kirim username
            startActivity(intent)
        }

        // AC Maintenance Handler
        val acMaintenance: CardView = findViewById(R.id.ac_maintenance_card)
        acMaintenance.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Maintenance")
            intent.putExtra("username", username)  // Kirim username
            startActivity(intent)
        }

        // AC Wash Handler
        val acWash: CardView = findViewById(R.id.ac_wash_card)
        acWash.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Wash")
            intent.putExtra("username", username)  // Kirim username
            startActivity(intent)
        }

        // Setup BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.activity -> {
                    // Pindah ke OrderListActivity dengan mengirimkan username
                    val intent = Intent(this@HomeActivity, OrderListActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this@HomeActivity, MapViewActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
