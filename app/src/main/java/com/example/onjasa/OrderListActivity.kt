package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderListActivity : AppCompatActivity() {

    private lateinit var orderAdapter: OrderAdapter
    private var ongoingOrders = listOf(
        Order("AC Installation", "Today, 11:30 AM", R.drawable.easy_installation__1_)

    )

    private var historyOrders = listOf(
        Order("AC Service", "Last week, 9:00 AM", R.drawable.maintenance_tools),
        Order("AC Wash", "2 weeks ago, 1:00 PM", R.drawable.spray),
        Order("AC Repair", "Yesterday, 3:00 PM", R.drawable.maintenance_tools)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        val rvOrders = findViewById<RecyclerView>(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        // Initial data with ongoing orders
        orderAdapter = OrderAdapter(ongoingOrders)
        rvOrders.adapter = orderAdapter

        val tvOngoing = findViewById<TextView>(R.id.tvShowOngoing)
        val tvHistory = findViewById<TextView>(R.id.tvShowHistory)

        // Inisialisasi: Set teks Ongoing berwarna biru dan History berwarna hitam
        tvOngoing.setTextColor(getColor(R.color.biru))
        tvHistory.setTextColor(getColor(R.color.black))

        // Set click listeners to change the displayed data and color
        tvOngoing.setOnClickListener {
            // Set Ongoing Orders
            tvOngoing.setTextColor(getColor(R.color.biru))
            tvHistory.setTextColor(getColor(R.color.black))

            orderAdapter.updateOrders(ongoingOrders)
        }

        tvHistory.setOnClickListener {
            // Set History Orders
            tvHistory.setTextColor(getColor(R.color.biru))  // Set warna teks History jadi biru
            tvOngoing.setTextColor(getColor(R.color.black)) // Set warna teks Ongoing jadi hitam

            orderAdapter.updateOrders(historyOrders)  // Update orders to history
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set listener untuk navigasi
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    true
                }
                R.id.activity -> {

                    true
                }
                R.id.chat -> {
                    // Handle Chat navigation (jika perlu)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this@OrderListActivity, ProfileActivity::class.java)
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }
    }




}
