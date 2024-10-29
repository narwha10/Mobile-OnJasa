package com.example.onjasa

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


        // Inisialisasi dan penggunaan elemen dari layout XML
        val profilePicture: ImageView = findViewById(R.id.profile_picture)
        val serviceTitle: TextView = findViewById(R.id.service_title)
        val serviceSubtitle: TextView = findViewById(R.id.service_subtitle)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Menetapkan teks sebagai contoh
        serviceTitle.text = "Halo, Galnigga"
        serviceSubtitle.text = "Kamu login sebagai Teknisi"
    }

    override fun onSupportNavigateUp(): Boolean {
        // Tidak ada navController, jadi kita bisa langsung mengembalikan nilai true
        return true
    }
}
