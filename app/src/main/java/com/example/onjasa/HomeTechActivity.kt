package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.databinding.ActivityHomeTechBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class HomeTechActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeTechBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeTechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi elemen dari layout XML menggunakan binding
        val profilePicture: ImageView = binding.profilePicture // Use binding to access views
        val serviceTitle: TextView = binding.serviceTitle // Use binding to access views
        val serviceSubtitle: TextView = binding.serviceSubtitle // Use binding to access views

        // Menetapkan teks sebagai contoh
        serviceTitle.text = "Halo, Galnigga"
        serviceSubtitle.text = "Kamu login sebagai Teknisi"

        // Set onClick listener for the profile picture to navigate to ProfileActivity
        profilePicture.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        // Tidak ada navController, jadi kita bisa langsung mengembalikan nilai true
        return true
    }
}
