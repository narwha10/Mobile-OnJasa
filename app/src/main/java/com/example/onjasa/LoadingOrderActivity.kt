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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoadingOrderActivity : AppCompatActivity() {

    private lateinit var countdownTextView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var orderId: String // Variable to store the order ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_order)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Retrieve technician name and price from Intent
        val technicianName = intent.getStringExtra("TECHNICIAN_NAME") ?: "Tidak ada Nama Teknisi"
        val technicianPrice = intent.getStringExtra("TECHNICIAN_PRICE") ?: "Tidak ada Harga"
        orderId = intent.getStringExtra("ORDER_ID") ?: "default_order_id" // Retrieve order ID from Intent

        // Find TextViews and set text
        val nameTextView: TextView = findViewById(R.id.namaTeknisi)
        val priceTextView: TextView = findViewById(R.id.hargaTeknisi)
        countdownTextView = findViewById(R.id.countdownTextView) // Ensure this ID exists in the layout

        nameTextView.text = technicianName
        priceTextView.text = technicianPrice

        // Set up countdown timer for 5 minutes (300,000 milliseconds)
        startCountdown(300000)

        // Find button and set OnClickListener
        val backToHomeButton: Button = findViewById(R.id.button)
        backToHomeButton.setOnClickListener {
            // Intent to switch to HomeActivity
            val intent = Intent(this, ACPaymentActivity::class.java)
            startActivity(intent)
            finish() // Close LoadingOrderActivity so it doesn't return when the back button is pressed
        }

        // Set insets for edge-to-edge display
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
                // Show Toast message
                Toast.makeText(this@LoadingOrderActivity, "Order Canceled", Toast.LENGTH_SHORT).show()

                // Update Firebase to change the order status to "canceled"
                updateOrderStatus(orderId, "canceled")

                // Intent to switch to HomeActivity
                val intent = Intent(this@LoadingOrderActivity, HomeActivity::class.java)
                startActivity(intent)
                finish() // Close LoadingOrderActivity so it doesn't return when the back button is pressed
            }
        }.start()
    }

    private fun updateOrderStatus(orderId: String, status: String) {
        // Update Firebase database to set order status to "canceled"
        database.child("orders").child(orderId).child("status").setValue(status)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Status updated successfully
                    Toast.makeText(this, "Order status updated to $status", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the error
                    Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
