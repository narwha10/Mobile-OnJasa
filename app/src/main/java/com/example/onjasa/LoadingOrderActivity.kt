package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class LoadingOrderActivity : AppCompatActivity() {

    private lateinit var countdownTextView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var orderId: String
    private lateinit var username: String
    private lateinit var technicianName: String
    private lateinit var technicianPrice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_order)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Retrieve username, technician name, and price from Intent
        username = intent.getStringExtra("USERNAME") ?: "Guest" // Initialize username from Intent
        technicianName = intent.getStringExtra("TECHNICIAN_NAME") ?: ""
        technicianPrice = intent.getStringExtra("TECHNICIAN_PRICE") ?: ""
        orderId = intent.getStringExtra("ORDER_ID") ?: "default_order_id"

        // Find TextViews and set text
        val nameTextView: TextView = findViewById(R.id.namaTeknisi)
        val priceTextView: TextView = findViewById(R.id.hargaTeknisi)
        countdownTextView = findViewById(R.id.countdownTextView)

        // Set the technician name and price
        nameTextView.text = technicianName
        priceTextView.text = technicianPrice

        // Check if both name and price are empty
        if (technicianName.isEmpty() && technicianPrice.isEmpty()) {
            // Hide the entire constraintLayout
            val constraintLayout: View = findViewById(R.id.constraintLayout)
            constraintLayout.visibility = View.GONE
        }

        // Set up countdown timer for 5 minutes (300,000 milliseconds)
        startCountdown(300000)

        // Find button and set OnClickListener
        val backToHomeButton: Button = findViewById(R.id.button)
        backToHomeButton.setOnClickListener {
            handleOrderClick(username) // Call the function to handle order click logic
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
                Toast.makeText(this@LoadingOrderActivity, "Order Canceled", Toast.LENGTH_SHORT).show()
                updateOrderStatus(orderId, "canceled")

                // Intent to switch to HomeActivity and pass username
                val intent = Intent(this@LoadingOrderActivity, HomeActivity::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    private fun updateOrderStatus(orderId: String, status: String) {
        database.child("orders").child(orderId).child("status").setValue(status)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Order status updated to $status", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // New method to handle order click logic when returning to Home
    private fun handleOrderClick(username: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .whereEqualTo("order_by", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.w("LoadingOrderActivity", "No orders found for user: $username")
                    // Navigate to HomeActivity if no orders are found
                    val intent = Intent(this@LoadingOrderActivity, HomeActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    finish()
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    val orderStatus = document.getString("status")
                    Log.d("LoadingOrderActivity", "Order status found: $orderStatus")

                    when (orderStatus) {
                        "processing" -> {
                            val intent = Intent(this@LoadingOrderActivity, LoadingOrderActivity::class.java)
                            intent.putExtra("USERNAME", username) // Pass username
                            intent.putExtra("TECHNICIAN_NAME", technicianName) // Pass technician name
                            intent.putExtra("TECHNICIAN_PRICE", technicianPrice) // Pass technician price
                            startActivity(intent)
                            finish()
                            return@addOnSuccessListener // Stop execution after navigation
                        }
                        "utiwi" -> {
                            val intent = Intent(this@LoadingOrderActivity, ACPaymentActivity::class.java)
                            intent.putExtra("USERNAME", username) // Pass username
                            intent.putExtra("TECHNICIAN_NAME", technicianName) // Pass technician name
                            intent.putExtra("TECHNICIAN_PRICE", technicianPrice) // Pass technician price
                            startActivity(intent)
                            finish()
                            return@addOnSuccessListener // Stop execution after navigation
                        }
                        else -> {
                            Log.w("LoadingOrderActivity", "Unknown status: $orderStatus")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoadingOrderActivity", "Error getting documents: ", exception)
            }
    }
}
