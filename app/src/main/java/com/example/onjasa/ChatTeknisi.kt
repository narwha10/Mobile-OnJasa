package com.example.onjasa

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var userId: String
    private lateinit var senderId: String
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_teknisi)

        // Inisialisasi Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Tangkap data dari Intent
        senderId = intent.getStringExtra("USERNAME") ?: "Anonymous"
        receiverId = intent.getStringExtra("TECHNICIAN_NAME") ?: "Technician"
        userId = receiverId // User yang sedang login dianggap sebagai penerima (receiver)

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerView)
        chatAdapter = ChatAdapter(userId = userId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Inisialisasi EditText dan Tombol Kirim
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.imageView11)

        // Tombol Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }

        // Cari percakapan berdasarkan senderId dan receiverId
        findChat()

        // Tambahkan pesan ke chat ketika tombol kirim ditekan
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotBlank() && chatId != null) {
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    senderId = senderId,
                    receiverId = receiverId,
                    content = messageText,
                    timestamp = System.currentTimeMillis()
                )
                saveMessageToFirestore(newMessage) // Simpan pesan ke Firestore
                messageEditText.text.clear() // Hapus teks setelah pesan dikirim

                // Scroll RecyclerView ke posisi terakhir (pesan terbaru)
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            } else {
                println("Chat ID is null or message is empty.")
            }
        }
    }

    /**
     * Fungsi untuk mencari percakapan (chatId)
     */
    private fun findChat() {
        val chatRef = firestore.collection("chats")

        // Periksa apakah percakapan sudah ada berdasarkan senderId dan receiverId
        chatRef.whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Jika dokumen ditemukan, gunakan chatId yang ada
                    chatId = querySnapshot.documents[0].id
                    fetchMessagesFromFirestore()
                } else {
                    // Jika tidak ditemukan, buat chat baru
                    val chatData = mapOf(
                        "senderId" to senderId,
                        "receiverId" to receiverId,
                        "timestamp" to System.currentTimeMillis()
                    )
                    chatRef.add(chatData)
                        .addOnSuccessListener { documentReference ->
                            chatId = documentReference.id
                            fetchMessagesFromFirestore()
                        }
                        .addOnFailureListener { e ->
                            println("Error creating chat: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error finding chat: ${e.message}")
            }
    }

    /**
     * Fungsi untuk membaca pesan secara real-time dari Firestore
     */


    private fun fetchMessagesFromFirestore() {
        if (chatId == null) {
            println("Chat ID is null. Cannot fetch messages.")
            return
        }

        val messageRef = firestore.collection("chats")
            .document(chatId!!)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        messageRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                println("Error fetching messages: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val messages = snapshot.toObjects(Message::class.java)
                chatAdapter.updateMessages(messages)  // Update adapter dengan pesan baru
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)  // Scroll ke pesan terbaru
            } else {
                println("No messages found.")
            }
        }
    }

    /**
     * Fungsi untuk menyimpan pesan ke Firestore
     */
    private fun saveMessageToFirestore(message: Message) {
        if (chatId == null) {
            println("Chat ID is null. Message cannot be saved.")
            return
        }

        val messageRef = firestore.collection("chats")
            .document(chatId!!)
            .collection("messages")

        messageRef.add(message)
            .addOnSuccessListener {
                println("Message saved successfully!")
            }
            .addOnFailureListener { e ->
                println("Error saving message: ${e.message}")
            }
    }
}
