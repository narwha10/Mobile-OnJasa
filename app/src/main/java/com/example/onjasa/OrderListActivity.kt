package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class OrderListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var ongoingOrders = mutableListOf<Pair<String, String>>() // List untuk menyimpan judul dan waktu
    private var historyOrders = mutableListOf<Pair<String, String>>() // List untuk menyimpan judul dan waktu
    private lateinit var username: String
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        // Ambil username dari Intent
        username = intent.getStringExtra("username") ?: ""

        val tvOrderTitle: TextView = findViewById(R.id.tvOrderTitle)
        tvOrderTitle.text = "Order by: $username"

        database = FirebaseDatabase.getInstance().getReference("orders")
        loadOrders(username)

        // Set listener untuk klik pada tvOrderTitle
        tvOrderTitle.setOnClickListener {
            Log.d("OrderListActivity", "tvOrderTitle clicked.")
            handleOrderClick(username)
        }
    }



    private fun loadOrders(username: String) {
        database.orderByChild("order_by").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ongoingOrders.clear()
                historyOrders.clear()
                Log.d("OrderListActivity", "Found ${snapshot.childrenCount} orders for user $username.")

                for (orderSnapshot in snapshot.children) {
                    val orderStatus = orderSnapshot.child("status").getValue(String::class.java) ?: ""
                    val title = orderSnapshot.child("order_by").getValue(String::class.java) ?: "No title"
                    val time = "Today, 11:30 AM" // Waktu yang diasumsikan untuk semua order

                    Log.d("OrderListActivity", "Order title: $title, status: $orderStatus")

                    if (orderStatus == "processing") {
                        ongoingOrders.add(Pair(title, time))
                    } else if (orderStatus == "utiwi") {
                        historyOrders.add(Pair(title, time))
                    }
                }

                orderAdapter.updateOrders(ongoingOrders)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderListActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun handleOrderClick(username: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .whereEqualTo("order_by", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.w("OrderListActivity", "No orders found for user: $username")
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    val orderStatus = document.getString("status")

                    Log.d("OrderListActivity", "Order status found: $orderStatus")

                    when (orderStatus) {
                        "processing" -> {
                            val intent = Intent(this@OrderListActivity, LoadingOrderActivity::class.java)
                            startActivity(intent)
                            return@addOnSuccessListener // Menghentikan eksekusi setelah navigasi
                        }
                        "utiwi" -> {
                            val intent = Intent(this@OrderListActivity, ACPaymentActivity::class.java)
                            startActivity(intent)
                            return@addOnSuccessListener // Menghentikan eksekusi setelah navigasi
                        }
                        else -> {
                            Log.w("OrderListActivity", "Unknown status: $orderStatus")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderListActivity", "Error getting documents: ", exception)
            }
    }


}
