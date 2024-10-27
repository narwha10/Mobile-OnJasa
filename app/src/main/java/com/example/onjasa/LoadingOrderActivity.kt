package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoadingOrderActivity : AppCompatActivity() {

    private lateinit var countdownTextView: TextView

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
        countdownTextView = findViewById(R.id.countdownTextView) // Pastikan ID ini ada di layout

        nameTextView.text = technicianName
        priceTextView.text = technicianPrice

        // Mengatur countdown timer selama 5 menit (300.000 milidetik)
        startCountdown(300000)

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

    private fun startCountdown(millisInFuture: Long) {
        object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                countdownTextView.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                countdownTextView.text = "Waktu Habis!"
                // Menampilkan Toast
                Toast.makeText(this@LoadingOrderActivity, "Order Canceled", Toast.LENGTH_SHORT).show()

                // Intent untuk berpindah ke HomeActivity
                val intent = Intent(this@LoadingOrderActivity, HomeActivity::class.java)
                startActivity(intent)
                finish() // Menutup LoadingOrderActivity agar tidak kembali ke layar ini saat tombol back ditekan
            }
        }.start()
    }
}
