package com.example.onjasa

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class HomeTechActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var notifMasukTextView: TextView
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tech)

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()
        notifMasukTextView = findViewById(R.id.notifmasuk)

        // Ambil username pengguna yang sedang login dari SharedPreferences
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        username = sharedPrefs.getString("username", "") ?: ""

        if (username.isNotEmpty()) {
            checkOrderNotification()
        } else {
            notifMasukTextView.visibility = View.VISIBLE
            notifMasukTextView.text = "TIDAK ADA ORDER"
        }
    }

    private fun checkOrderNotification() {
        // Query untuk mencari order dengan technician_name yang sesuai dengan username pengguna login
        db.collection("orders")
            .whereEqualTo("technician_name", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    // Jika ada order yang sesuai, tampilkan pesan "ORDER MASUK"
                    notifMasukTextView.visibility = View.VISIBLE
                    notifMasukTextView.text = "ORDER MASUK"
                } else {
                    // Jika tidak ada order yang cocok, tampilkan "TIDAK ADA ORDER"
                    notifMasukTextView.visibility = View.VISIBLE
                    notifMasukTextView.text = "TIDAK ADA ORDER"
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching orders", e)
                // Jika terjadi kesalahan dalam akses Firestore, tampilkan "TIDAK ADA ORDER"
                notifMasukTextView.visibility = View.VISIBLE
                notifMasukTextView.text = "TIDAK ADA ORDER"
            }
    }
}
