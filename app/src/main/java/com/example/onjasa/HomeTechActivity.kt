package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.view.View // Import View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.databinding.ActivityHomeTechBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HomeTechActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeTechBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra("username")
        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()

        binding = ActivityHomeTechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Inisialisasi elemen dari layout XML menggunakan binding
        val profilePicture: ImageView = binding.profilePicture
        val serviceTitle: TextView = binding.serviceTitle
        val serviceSubtitle: TextView = binding.serviceSubtitle
        val incomingNotificationCard: View = binding.incomingNotificationCard // Card untuk notifikasi masuk
        val noOrdersText: TextView = binding.noOrdersText // TextView untuk "tidak ada order masuk"

        // Menetapkan teks sebagai contoh
        serviceTitle.text = "Halo, $username"
        serviceSubtitle.text = "Kamu login sebagai Teknisi"

        // Set onClick listener untuk profile picture untuk navigate ke ProfileActivity
        profilePicture.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Memanggil fungsi untuk cek orders di Firebase
        checkIncomingOrders(username, incomingNotificationCard, noOrdersText)
    }

    private fun checkIncomingOrders(username: String?, incomingNotificationCard: View, noOrdersText: TextView) {
        if (username == null) {
            Toast.makeText(this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("orders")
            .whereEqualTo("technician_name", username)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Jika ada order untuk teknisi, tampilkan card notifikasi masuk
                    incomingNotificationCard.visibility = View.VISIBLE
                    noOrdersText.visibility = View.GONE
                } else {
                    // Jika tidak ada order, tampilkan teks "tidak ada order masuk"
                    incomingNotificationCard.visibility = View.GONE
                    noOrdersText.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                // Menangani error
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return true
    }
}
