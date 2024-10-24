// OrderACRepairActivity.kt
package com.example.onjasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class OrderACRepairActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_acrepair)

        // Receive data from HomeActivity
        val serviceType = intent.getStringExtra("service_type")

        // Update the header TextView and ImageView based on the received data
        val serviceTitleTextView: TextView = findViewById(R.id.textView5)
        serviceTitleTextView.text = serviceType ?: "Service"  // Default to "Service" if null

        val serviceImageView: ImageView = findViewById(R.id.imageView5)
        val imageResId: Int = when (serviceType) {
            "AC Repair" -> R.drawable.maintenance_tools
            "AC Installation" -> R.drawable.easy_installation
            "AC Maintenance" -> R.drawable.ac
            "AC Wash" -> R.drawable.spray
            else -> R.drawable.order_processing // Default image
        }
        serviceImageView.setImageResource(imageResId)

        // Handle Back button click
        val btnBack: ImageView = findViewById(R.id.imageViewBack)
        btnBack.setOnClickListener {
            val intent = Intent(this@OrderACRepairActivity, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Handle Make Order button click
        val btnMakeOrder: Button = findViewById(R.id.btnMakeOrder)
        btnMakeOrder.setOnClickListener {
            val intent = Intent(this@OrderACRepairActivity, ACRepairActivity::class.java)
            intent.putExtra("header_title", serviceTitleTextView.text.toString())  // Pass header text
            intent.putExtra("header_image", imageResId)  // Pass image resource ID
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
