package com.example.onjasa

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class OrderListActivity : AppCompatActivity() {

    private lateinit var orderHistoryTextView: TextView
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        // Retrieve username from Intent
        username = intent.getStringExtra("USERNAME") ?: "Guest" // Default to "Guest"
        Log.d("OrderListActivity", "Received username: $username") // Logging untuk memverifikasi username

        // Initialize TextView to display order history
        orderHistoryTextView = findViewById(R.id.orderHistoryTextView)

        // Load and display order history for the user
        loadOrderHistory(username)
    }

    private fun loadOrderHistory(username: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .whereEqualTo("order_by", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("OrderListActivity", "Number of orders found: ${querySnapshot.size()}") // Logging jumlah pesanan yang ditemukan

                if (querySnapshot.isEmpty) {
                    Log.w("OrderListActivity", "No orders found for user: $username")
                    orderHistoryTextView.text = "No order history available."
                    return@addOnSuccessListener
                }

                val orderHistoryBuilder = StringBuilder()
                var foundRelevantOrder = false // Flag to check if we found relevant orders

                for (document in querySnapshot.documents) {
                    val orderStatus = document.getString("status")
                    Log.d("OrderListActivity", "Order ID: ${document.id}, Status found: $orderStatus")

                    // Only show orders with status "done" or "canceled"
                    if (orderStatus != null && (orderStatus == "done" || orderStatus == "canceled")) {
                        foundRelevantOrder = true // Set flag to true if we find a relevant order
                        orderHistoryBuilder.append("Order ID: ${document.id}, Status: $orderStatus\n")
                    } else {
                        Log.w("OrderListActivity", "Skipping order with unknown or irrelevant status: $orderStatus")
                    }
                }

                // Display the accumulated order history
                orderHistoryTextView.text = if (foundRelevantOrder) {
                    orderHistoryBuilder.toString()
                } else {
                    "No completed or canceled orders."
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderListActivity", "Error getting documents: ", exception)
                orderHistoryTextView.text = "Failed to load order history."
            }
    }
}
