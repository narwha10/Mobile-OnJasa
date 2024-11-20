package com.example.onjasa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.FirebaseFirestore

class OrderACRepairActivity : AppCompatActivity() {

    private lateinit var username: String
    private lateinit var etAlamat: EditText
    private lateinit var etNohp: EditText
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var namaUserTextView: TextView
    private lateinit var alamatUserTextView: TextView
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_acrepair)

        // Menerima data dari HomeActivity
        val serviceType = intent.getStringExtra("service_type")
        username = intent.getStringExtra("username") ?: "Guest"

        Log.d("OrderACRepairActivity", "Service Type: $serviceType")
        Log.d("OrderACRepairActivity", "Username: $username")

        // Mengatur teks header dan gambar berdasarkan service type
        val serviceTitleTextView: TextView = findViewById(R.id.textView5)
        serviceTitleTextView.text = serviceType ?: "Service"

        val serviceImageView: ImageView = findViewById(R.id.imageView5)
        val imageResId: Int = getServiceImageResource(serviceType)
        serviceImageView.setImageResource(imageResId)

        Log.d("OrderACRepairActivity", "Image Resource ID: $imageResId")

        // Inisialisasi EditText untuk alamat dan nohp
        etAlamat = findViewById(R.id.etAlamat)
        etNohp = findViewById(R.id.etNohp)

        // Menangani klik tombol Kembali (Back)
        val btnBack: ImageView = findViewById(R.id.imageViewBack)
        btnBack.setOnClickListener {
            Log.d("OrderACRepairActivity", "Back button clicked")
            navigateToHomeActivity()
        }

        // Menginisialisasi UI untuk nama dan alamat
        namaUserTextView = findViewById(R.id.nama)
        alamatUserTextView = findViewById(R.id.alamat)

        // Tampilkan username
        namaUserTextView.text = username

        // Ambil data alamat dari Firestore
        getUserData(username)

        // Menangani tombol "Use Yours"
        constraintLayout = findViewById(R.id.constraintLayout)
        val btnUseYours: Button = findViewById(R.id.filledButton)
        btnUseYours.setOnClickListener {
            Log.d("OrderACRepairActivity", "Use Yours button clicked")
            constraintLayout.visibility = View.VISIBLE
        }

        // Menangani tombol "Make Order"
        val btnMakeOrder: Button = findViewById(R.id.btnMakeOrder)
        btnMakeOrder.setOnClickListener {
            handleMakeOrder(serviceTitleTextView.text.toString(), imageResId)
        }
    }

    // Fungsi untuk mengambil gambar berdasarkan tipe layanan
    private fun getServiceImageResource(serviceType: String?): Int {
        return when (serviceType) {
            "AC Repair" -> R.drawable.maintenance_tools
            "AC Installation" -> R.drawable.easy_installation
            "AC Maintenance" -> R.drawable.ac
            "AC Wash" -> R.drawable.spray
            else -> R.drawable.order_processing
        }
    }

    // Fungsi untuk navigasi ke HomeActivity
    private fun navigateToHomeActivity() {
        val intent = Intent(this@OrderACRepairActivity, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Fungsi untuk menangani pengiriman data "Make Order"
    private fun handleMakeOrder(serviceTitle: String, imageResId: Int) {
        val alamat = etAlamat.text.toString().trim()
        val nohp = etNohp.text.toString().trim()

        Log.d("OrderACRepairActivity", "Alamat: $alamat")
        Log.d("OrderACRepairActivity", "No HP: $nohp")

        if (alamat.isEmpty() || nohp.isEmpty()) {
            Toast.makeText(this, "Harap isi alamat dan nomor HP!", Toast.LENGTH_SHORT).show()
            Log.d("OrderACRepairActivity", "Alamat atau No HP kosong")
            return
        }

        val intent = Intent(this@OrderACRepairActivity, ACRepairActivity::class.java)
        intent.putExtra("header_title", serviceTitle)
        intent.putExtra("header_image", imageResId)
        intent.putExtra("username", username)
        intent.putExtra("etalamat", alamat)
        intent.putExtra("etnohp", nohp)

        Log.d("OrderACRepairActivity", "Sending data to ACRepairActivity: Alamat: $alamat, No HP: $nohp")

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // Fungsi untuk mengambil data pengguna dari Firestore
    private fun getUserData(username: String) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val alamat = document.getString("alamat")
                    alamatUserTextView.text = alamat ?: "Alamat tidak ditemukan"
                } else {
                    Log.d("Firestore", "No such document!")
                    alamatUserTextView.text = "Alamat tidak ditemukan"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting documents: ", exception)
                alamatUserTextView.text = "Error retrieving address"
            }
    }
}
