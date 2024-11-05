package com.example.onjasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ACRepairActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acrepair)

        // Retrieve and set header title and image
        val headerTitle = intent.getStringExtra("header_title") ?: "No Title"
        val headerImageResId = intent.getIntExtra("header_image", R.drawable.order_processing)

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
        setupTeknisiCards()


    }
    // Pada bagian di mana kita ingin menambahkan kartu teknisi
    private fun setupTeknisiCards() {
        val contentContainer: LinearLayout = findViewById(R.id.contentContainer) // Container untuk menambahkan kartu

        // Ambil data teknisi dari Firestore
        db.collection("technician")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    var cardCount = 0

                    documents?.forEach { document ->
                        if (cardCount < 3) { // Batasi hingga 3 kartu
                            val cardView = layoutInflater.inflate(R.layout.card_teknisi, contentContainer, false)
                            val namaTeknisiTextView: TextView = cardView.findViewById(R.id.namaTeknisi)
                            val hargaTeknisiTextView: TextView = cardView.findViewById(R.id.hargaTeknisi)

                            val namaTeknisi = document.getString("nama") ?: "Nama tidak tersedia"
                            val hargaTeknisi = document.getString("harga") ?: "Harga tidak tersedia"

                            namaTeknisiTextView.text = namaTeknisi
                            hargaTeknisiTextView.text = hargaTeknisi

                            cardView.setOnClickListener {
                                saveOrderToFirestore(namaTeknisi, hargaTeknisi)
                            }

                            contentContainer.addView(cardView) // Tambahkan kartu ke kontainer
                            cardCount++
                        }
                    }
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.exception)
                }
            }
    }



    private fun saveOrderToFirestore(nama: String?, harga: String?) {
        // Prepare order data
        val orderData = hashMapOf(
            "technician_name" to nama,
            "technician_price" to harga,
            "order_timestamp" to Date(),
            "status" to "processing" // or "pending"
        )

        // Save to Firestore in "orders" collection
        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Order added with ID: ${documentReference.id}")
                Toast.makeText(this, "Order berhasil!", Toast.LENGTH_SHORT).show()

                // Proceed to LoadingOrderActivity with technician details
                val intent = Intent(this, LoadingOrderActivity::class.java).apply {
                    putExtra("TECHNICIAN_NAME", nama)
                    putExtra("TECHNICIAN_PRICE", harga)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding order", e)
                Toast.makeText(this, "Gagal order. Coba lagi!", Toast.LENGTH_SHORT).show()
            }
    }
}
