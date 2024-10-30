package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.databinding.ActivityHomeTechBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class HomeTechActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeTechBinding
    private lateinit var firestore: FirebaseFirestore

    private var currentOrderId: String? = null // Menyimpan ID order saat ini untuk di-update

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
        val orderDescription: TextView = binding.orderDescription // TextView untuk menampilkan deskripsi order
        val btnAccept: Button = binding.btnAccept // Tombol Accept
        val btnReject: Button = binding.btnReject // Tombol Reject

        // Menetapkan teks sebagai contoh
        serviceTitle.text = "Halo, $username"
        serviceSubtitle.text = "Kamu login sebagai Teknisi"

        // Set onClick listener untuk profile picture untuk navigate ke ProfileActivity
        profilePicture.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Memanggil fungsi untuk cek orders di Firebase
        checkIncomingOrders(username, incomingNotificationCard, noOrdersText, orderDescription, btnAccept, btnReject)
    }

    private fun checkIncomingOrders(
        username: String?,
        incomingNotificationCard: View,
        noOrdersText: TextView,
        orderDescription: TextView,
        btnAccept: Button,
        btnReject: Button
    ) {
        if (username == null) {
            Toast.makeText(this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        // Query untuk mengambil satu order dengan status "processing"
        firestore.collection("orders")
            .whereEqualTo("technician_name", username)
            .whereEqualTo("status", "processing")
            .limit(1) // Ambil hanya satu order
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Jika ada order untuk teknisi, tampilkan card notifikasi masuk
                    incomingNotificationCard.visibility = View.VISIBLE
                    noOrdersText.visibility = View.GONE

                    // Ambil nilai dari field "order_by" pada dokumen yang ada
                    for (document: QueryDocumentSnapshot in querySnapshot) {
                        currentOrderId = document.id // Simpan ID order saat ini
                        val orderBy = document.getString("order_by") // Ambil nilai order_by
                        orderDescription.text = orderBy ?: "Tidak ada informasi order" // Tampilkan informasi order
                    }

                    // Set onClick listeners untuk tombol Accept dan Reject
                    btnAccept.setOnClickListener {
                        currentOrderId?.let { orderId ->
                            updateOrderStatus(orderId, "utiwi") // Update status menjadi "utiwi"
                        }
                    }

                    btnReject.setOnClickListener {
                        currentOrderId?.let { orderId ->
                            updateOrderStatus(orderId, "canceled") // Update status menjadi "canceled"
                        }
                    }
                } else {
                    // Jika tidak ada order, tampilkan teks "tidak ada order masuk"
                    incomingNotificationCard.visibility = View.GONE
                    noOrdersText.visibility = View.VISIBLE
                    orderDescription.text = "" // Kosongkan order description jika tidak ada order
                }
            }
            .addOnFailureListener { e ->
                // Menangani error
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderStatus(orderId: String, newStatus: String) {
        firestore.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Status order telah diperbarui menjadi $newStatus", Toast.LENGTH_SHORT).show()
                // Refresh UI atau lakukan hal lain setelah pembaruan
                checkIncomingOrders(intent.getStringExtra("username"), binding.incomingNotificationCard, binding.noOrdersText, binding.orderDescription, binding.btnAccept, binding.btnReject) // Refresh the incoming orders
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return true
    }
}
