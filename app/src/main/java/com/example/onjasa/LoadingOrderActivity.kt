package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoadingOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_order)

        // Ambil nama teknisi dan harga dari Intent
        val technicianName = intent.getStringExtra("TECHNICIAN_NAME") ?: "Tidak ada Nama Teknisi"
        val technicianPrice = intent.getStringExtra("TECHNICIAN_PRICE") ?: "Tidak ada Harga"

        // Temukan TextViews dan atur teks
        val nameTextView: TextView = findViewById(R.id.namaTeknisi)
        val priceTextView: TextView = findViewById(R.id.hargaTeknisi)

        nameTextView.text = technicianName
        priceTextView.text = technicianPrice

        // Temukan tombol dan atur OnClickListener
        val backToHomeButton: Button = findViewById(R.id.button)
        backToHomeButton.setOnClickListener {
            // Intent untuk berpindah ke HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Menutup LoadingOrderActivity agar tidak kembali ke layar ini saat tombol back ditekan
        }

        // Atur insets untuk tampilan edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
