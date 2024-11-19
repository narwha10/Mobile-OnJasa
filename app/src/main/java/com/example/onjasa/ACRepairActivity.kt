package com.example.onjasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private lateinit var namaTeknisiTextView: TextView
    private lateinit var namaTeknisiTextView2: TextView
    private lateinit var namaTeknisiTextView3: TextView
    private lateinit var hargaTeknisiTextView: TextView
    private lateinit var hargaTeknisiTextView2: TextView
    private lateinit var hargaTeknisiTextView3: TextView

    private lateinit var username: String
    private var alamat: String? = null
    private var nohp: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acrepair)

        val headerTitle = intent.getStringExtra("header_title") ?: "No Title"
        val headerImageResId = intent.getIntExtra("header_image", R.drawable.order_processing)
        val headerTitleTextView: TextView = findViewById(R.id.headertitle)
        headerTitleTextView.text = headerTitle

        val headerImageView: ImageView = findViewById(R.id.imgheader)
        headerImageView.setImageResource(headerImageResId)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        // Inisialisasi TextView teknisi
        namaTeknisiTextView = findViewById(R.id.namaTeknisiTextView)
        namaTeknisiTextView2 = findViewById(R.id.namaTeknisiTextView2)
        namaTeknisiTextView3 = findViewById(R.id.namaTeknisiTextView3)
        hargaTeknisiTextView = findViewById(R.id.hargaTeknisiTextView)
        hargaTeknisiTextView2 = findViewById(R.id.hargaTeknisiTextView2)
        hargaTeknisiTextView3 = findViewById(R.id.hargaTeknisiTextView3)

        username = intent.getStringExtra("username") ?: "Guest"

        // Logika 2: Cek apakah alamat dan nohp dikirim dari OrderACRepairActivity
        alamat = intent.getStringExtra("alamat")
        nohp = intent.getStringExtra("nohp")

        // Jika alamat dan nohp tidak ada di Intent, gunakan Logika 1 untuk mengambil dari Firestore
        if (alamat.isNullOrEmpty() || nohp.isNullOrEmpty()) {
            // Logika 1: Ambil data alamat dan nohp dari Firestore
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userDoc = documents.first()
                        alamat = userDoc.getString("alamat") ?: "Alamat tidak ditemukan"
                        nohp = userDoc.getString("nohp") ?: "No HP tidak ditemukan"
                    } else {
                        Toast.makeText(this, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error getting user data", e)
                    Toast.makeText(this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
                }
        }

        // Ambil data teknisi dari Firestore
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

                    // Set TextView teknisi dari Firestore
                    namaTeknisiTextView.text = namaTeknisiList.getOrNull(0) ?: "No data"
                    namaTeknisiTextView2.text = namaTeknisiList.getOrNull(1) ?: "No data"
                    namaTeknisiTextView3.text = namaTeknisiList.getOrNull(2) ?: "No data"

                    hargaTeknisiTextView.text = hargaTeknisiList.getOrNull(0) ?: "No data"
                    hargaTeknisiTextView2.text = hargaTeknisiList.getOrNull(1) ?: "No data"
                    hargaTeknisiTextView3.text = hargaTeknisiList.getOrNull(2) ?: "No data"

                    // Setup listeners untuk setiap kartu teknisi
                    setClickListeners(
                        findViewById(R.id.contentCardTeknisi),
                        namaTeknisiList.getOrNull(0),
                        hargaTeknisiList.getOrNull(0)
                    )
                    setClickListeners(
                        findViewById(R.id.contentCardTeknisi2),
                        namaTeknisiList.getOrNull(1),
                        hargaTeknisiList.getOrNull(1)
                    )
                    setClickListeners(
                        findViewById(R.id.contentCardTeknisi3),
                        namaTeknisiList.getOrNull(2),
                        hargaTeknisiList.getOrNull(2)
                    )
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.exception)
                }
            }
    }

    private fun setClickListeners(
        card: LinearLayout, nama: String?, harga: String?
    ) {
        card.setOnClickListener {
            saveOrderToFirestore(nama, harga)
        }
    }

    private fun saveOrderToFirestore(nama: String?, harga: String?) {
        // Logika utama: gunakan data alamat dan nohp dari Intent jika ada
        if (alamat.isNullOrEmpty() || nohp.isNullOrEmpty()) {
            Toast.makeText(this, "Data alamat atau nohp tidak lengkap.", Toast.LENGTH_SHORT).show()
            return
        }

        val orderData = hashMapOf(
            "technician_name" to nama,
            "technician_price" to harga,
            "order_timestamp" to Date(),
            "order_by" to username,
            "alamat" to alamat,
            "nohp" to nohp,
            "status" to "processing"
        )

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Order berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoadingOrderActivity::class.java).apply {
                    putExtra("USERNAME", username) // Pass username
                    putExtra("ORDER_ID", documentReference.id)
                    putExtra("TECHNICIAN_NAME", nama)
                    putExtra("TECHNICIAN_PRICE", harga)
                }
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error adding order", e)
            }
    }
}
