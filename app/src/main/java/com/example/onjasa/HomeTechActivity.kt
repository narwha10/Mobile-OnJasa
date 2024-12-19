package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.databinding.ActivityHomeTechBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeTechActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeTechBinding
    private lateinit var firestore: FirebaseFirestore

    private var currentOrderId: String? = null // Menyimpan ID order saat ini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username")
        Toast.makeText(this, "Welcome $username", Toast.LENGTH_SHORT).show()

        binding = ActivityHomeTechBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Set teks nama teknisi
        binding.serviceTitle.text = "Halo, $username"
        binding.serviceSubtitle.text = "Kamu login sebagai Teknisi"

        // Navigasi ke profil saat gambar diklik
        binding.profilePicture.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        checkIncomingOrders(username)
    }

    private fun checkIncomingOrders(username: String?) {
        if (username == null) {
            Toast.makeText(this, "Username tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        // Mendengarkan perubahan data secara real-time
        firestore.collection("orders")
            .whereEqualTo("technician_name", username)
            .whereIn("status", listOf("processing", "utiwi")) // Memfilter status
            .limit(1)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        currentOrderId = document.id
                        val orderBy = document.getString("order_by") ?: "Unknown"
                        val status = document.getString("status")
                        val chatId = document.getString("chatId") ?: ""  // Mengambil chatId dari dokumen order

                        when (status) {
                            "processing" -> {
                                binding.orderCard.visibility = View.VISIBLE
                                binding.noOrdersText.visibility = View.GONE
                                binding.orderTitle.text = "New Order Request"
                                binding.orderDescription.text = orderBy

                                binding.actionButtons.visibility = View.VISIBLE
                                binding.btnAccept.setOnClickListener {
                                    updateOrderStatus(currentOrderId, "utiwi")
                                }
                                binding.btnReject.setOnClickListener {
                                    updateOrderStatus(currentOrderId, "rejected")
                                }
                            }

                            "utiwi" -> {
                                binding.orderCard.visibility = View.VISIBLE
                                binding.noOrdersText.visibility = View.GONE
                                binding.orderTitle.text = "Your OnGoing Orders:"
                                binding.orderDescription.text = orderBy

                                binding.actionButtons.visibility = View.GONE
                                binding.orderCard.setOnClickListener {
                                    val intent = Intent(this, MapViewActivity::class.java)
                                    intent.putExtra("orderId", currentOrderId) // Mengirimkan orderId
                                    intent.putExtra("USERNAME", username)
                                    intent.putExtra("orderBy", orderBy) // Mengirimkan nilai orderBy
                                    intent.putExtra("chatId", chatId) // Mengirimkan chatId yang benar

                                    Log.d("ChatTeknisi", "Chat ID: $chatId")
                                    Log.d("MapViewActivity", "Username dikirim: $username, OrderBy: $orderBy, chatId: $chatId")
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                } else {
                    binding.orderCard.visibility = View.GONE
                    binding.noOrdersText.visibility = View.VISIBLE
                }
            }
    }


    private fun updateOrderStatus(orderId: String?, newStatus: String) {
        if (orderId == null) return

        firestore.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Order updated to $newStatus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update order: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}