package com.example.onjasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class OrderACRepairActivity : AppCompatActivity() {

    private lateinit var username: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_acrepair)

        // Menerima data dari HomeActivity
        val serviceType = intent.getStringExtra("service_type")
        username = intent.getStringExtra("username") ?: "Guest"

        Log.d("OrderACRepairActivity", "Service Type: $serviceType")
        Log.d("OrderACRepairActivity", "Username: $username")

        // Mengatur teks header dan gambar berdasarkan jenis layanan
        val serviceTitleTextView: TextView = findViewById(R.id.textView5)
        serviceTitleTextView.text = serviceType ?: "Service"

        val serviceImageView: ImageView = findViewById(R.id.imageView5)
        val imageResId: Int = when (serviceType) {
            "AC Repair" -> R.drawable.maintenance_tools
            "AC Installation" -> R.drawable.easy_installation
            "AC Maintenance" -> R.drawable.ac
            "AC Wash" -> R.drawable.spray
            else -> R.drawable.order_processing
        }
        serviceImageView.setImageResource(imageResId)

        Log.d("OrderACRepairActivity", "Image Resource ID: $imageResId")


        // Menangani klik tombol Kembali (Back)
        val btnBack: ImageView = findViewById(R.id.imageViewBack)
        btnBack.setOnClickListener {
            Log.d("OrderACRepairActivity", "Back button clicked")
            val intent = Intent(this@OrderACRepairActivity, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Menangani tombol "Use Yours"
        val btnMakeOrder: Button = findViewById(R.id.btnMakeOrder)
        btnMakeOrder.setOnClickListener {
            Log.d("OrderACRepairActivity", "Use Yours button clicked")
            val intent = Intent(this@OrderACRepairActivity, ACRepairActivity::class.java)
            intent.putExtra("header_title", serviceTitleTextView.text.toString())
            intent.putExtra("header_image", imageResId)
            intent.putExtra("username", username)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }
}
