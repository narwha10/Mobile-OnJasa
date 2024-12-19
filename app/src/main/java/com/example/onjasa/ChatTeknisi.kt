package com.example.onjasa

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onjasa.models.ChatAdapter
import com.example.onjasa.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatTeknisi : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView

    private var chatId: String? = null
    private lateinit var senderId: String
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_teknisi)

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        // Tangkap data dari Intent
        chatId = intent.getStringExtra("chatId")
        senderId = intent.getStringExtra("USERNAME") ?: "Anonymous"
        receiverId = intent.getStringExtra("TECHNICIAN_NAME") ?: "Technician"

        Log.d("ChatTeknisi", "Chat ID: $chatId")

        // Validasi chatId
        if (chatId == null) {
            Toast.makeText(this, "Chat ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
            finish()  // Kembali ke activity sebelumnya jika chatId tidak valid
            return
        }

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerView)
        chatAdapter = ChatAdapter(userId = senderId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Inisialisasi EditText dan Tombol Kirim
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.imageView11)

        // Tombol Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()  // Kembali ke activity sebelumnya
        }

        // Dengarkan pesan di Firestore
        fetchMessagesFromFirestore()

        // Kirim pesan ketika tombol diklik
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank()) {
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    senderId = senderId,
                    receiverId = receiverId,
                    content = messageText,
                    timestamp = System.currentTimeMillis()
                )
                saveMessageToFirestore(newMessage)
                messageEditText.text.clear()
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }

    /**
     * Fungsi untuk menyimpan pesan ke Firestore sebagai sub-dokumen dari chat
     */
    private fun saveMessageToFirestore(message: Message) {
        // Pastikan chatId valid
        if (chatId.isNullOrEmpty()) {
            Toast.makeText(this, "Chat ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Debugging: Periksa nilai chatId
        Log.d("ChatTeknisi", "Chat ID: $chatId")

        // Pastikan chatId tidak kosong sebelum melanjutkan
        val chatRef = firestore.collection("chats").document(chatId!!) // Asumsikan chatId tidak null
        val messageRef = chatRef.collection("messages")

        val messageData = mapOf(
            "senderId" to message.senderId,
            "receiverId" to message.receiverId,
            "content" to message.content,
            "timestamp" to message.timestamp
        )

        messageRef.add(messageData)
            .addOnSuccessListener {
                Log.d("ChatTeknisi", "Message saved successfully!")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengirim pesan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchMessagesFromFirestore() {
        // Pastikan chatId valid
        if (chatId.isNullOrEmpty()) {
            Toast.makeText(this, "Chat ID tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val chatRef = firestore.collection("chats").document(chatId ?: "") // Memastikan chatId valid
        val messageRef = chatRef.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        messageRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Toast.makeText(this, "Gagal memuat pesan: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val messages = snapshot?.documents?.map { doc ->
                Message(
                    id = doc.id,
                    senderId = doc.getString("senderId") ?: "",
                    receiverId = doc.getString("receiverId") ?: "",
                    content = doc.getString("content") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            } ?: emptyList()

            chatAdapter.updateMessages(messages)
            recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }
}
