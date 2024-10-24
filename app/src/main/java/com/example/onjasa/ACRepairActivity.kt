package com.example.onjasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class ACRepairActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    private lateinit var namaTeknisiTextView: TextView
    private lateinit var namaTeknisiTextView2: TextView
    private lateinit var namaTeknisiTextView3: TextView
    private lateinit var hargaTeknisiTextView: TextView
    private lateinit var hargaTeknisiTextView2: TextView
    private lateinit var hargaTeknisiTextView3: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acrepair)

        // Retrieve and set header title and image
        val headerTitle = intent.getStringExtra("header_title") ?: "No Title"
        val headerImageResId = intent.getIntExtra("header_image", R.drawable.order_processing)

        // Set header title and image to views
        val headerTitleTextView: TextView = findViewById(R.id.headertitle)
        headerTitleTextView.text = headerTitle

        val headerImageView: ImageView = findViewById(R.id.imgheader)
        headerImageView.setImageResource(headerImageResId)

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle Cancel Order button
        val btnCancelOrder: Button = findViewById(R.id.btncancelorder)
        btnCancelOrder.setOnClickListener {
            val intent = Intent(this@ACRepairActivity, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Firestore Initialization
        db = FirebaseFirestore.getInstance()

        // Initialize TextViews
        namaTeknisiTextView = findViewById(R.id.namaTeknisiTextView)
        namaTeknisiTextView2 = findViewById(R.id.namaTeknisiTextView2)
        namaTeknisiTextView3 = findViewById(R.id.namaTeknisiTextView3)
        hargaTeknisiTextView = findViewById(R.id.hargaTeknisiTextView)
        hargaTeknisiTextView2 = findViewById(R.id.hargaTeknisiTextView2)
        hargaTeknisiTextView3 = findViewById(R.id.hargaTeknisiTextView3)

        // Retrieve data from Firestore
        db.collection("technician")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    val namaTeknisiList = mutableListOf<String>()
                    val hargaTeknisiList = mutableListOf<String>()

                    documents?.forEach { document ->
                        val nama = document.getString("nama")
                        val harga = document.getString("harga")

                        if (nama != null && harga != null) {
                            namaTeknisiList.add(nama)
                            hargaTeknisiList.add(harga)
                        } else {
                            Log.e("FirestoreError", "Missing 'nama' or 'harga' in document: ${document.id}")
                        }
                    }

                    // Update TextViews with Firestore data
                    namaTeknisiTextView.text = namaTeknisiList.getOrNull(0) ?: "No data"
                    namaTeknisiTextView2.text = namaTeknisiList.getOrNull(1) ?: "No data"
                    namaTeknisiTextView3.text = namaTeknisiList.getOrNull(2) ?: "No data"

                    hargaTeknisiTextView.text = hargaTeknisiList.getOrNull(0) ?: "No data"
                    hargaTeknisiTextView2.text = hargaTeknisiList.getOrNull(1) ?: "No data"
                    hargaTeknisiTextView3.text = hargaTeknisiList.getOrNull(2) ?: "No data"
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.exception)
                    namaTeknisiTextView.text = "Error loading data"
                }
            }
    }
}

