package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var btnlogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


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
            startActivity(intent)
        }

        // AC Installation Handler
        val acInstallation: CardView = findViewById(R.id.ac_installation_card)
        acInstallation.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Installation")
            startActivity(intent)
        }

        // AC Maintenance Handler
        val acMaintenance: CardView = findViewById(R.id.ac_maintenance_card)
        acMaintenance.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Maintenance")
            startActivity(intent)
        }

        // AC Wash Handler
        val acWash: CardView = findViewById(R.id.ac_wash_card)
        acWash.setOnClickListener {
            val intent = Intent(this@HomeActivity, OrderACRepairActivity::class.java)
            intent.putExtra("service_type", "AC Wash")
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
                    val intent = Intent(this@HomeActivity, OrderListActivity::class.java)
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
