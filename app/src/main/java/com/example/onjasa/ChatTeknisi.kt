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
        val ordersRef = firestore.collection("orders")

        // Cari apakah sudah ada order dengan senderId atau receiverId di dalam field 'order_by' atau 'technician_name'
        ordersRef.whereEqualTo("order_by", senderId)
            .whereEqualTo("technician_name", receiverId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    // Jika percakapan sudah ada, ambil chatId dari order yang ditemukan
                    val orderDocument = querySnapshot.documents[0]
                    val existingChatId = orderDocument.getString("chat_id")
                    if (existingChatId != null) {
                        // Jika sudah ada chatId yang terhubung dengan order ini, gunakan chatId yang sudah ada
                        chatId = existingChatId
                        fetchMessagesFromFirestore()
                    } else {
                        // Jika belum ada chatId, buat chat baru
                        createNewChat()
                    }
                } else {
                    // Jika tidak ada order yang sesuai, buat chat baru
                    createNewChat()
                }
            }
            .addOnFailureListener { e ->
                println("Error finding chat: ${e.message}")
                createNewChat() // Jika gagal mencari order, buat chat baru
            }
    }

    private fun createNewChat() {
        val chatRef = firestore.collection("chats")

        // Buat chat baru
        val chatData = mapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "timestamp" to System.currentTimeMillis()
        )

        // Tambahkan chat baru ke Firestore
        chatRef.add(chatData)
            .addOnSuccessListener { documentReference ->
                chatId = documentReference.id
                updateOrderWithChatId() // Update order dengan chatId baru
                fetchMessagesFromFirestore()
            }
            .addOnFailureListener { e ->
                println("Error creating chat: ${e.message}")
            }
    }

    private fun updateOrderWithChatId() {
        val ordersRef = firestore.collection("orders")

        // Update order dengan chat_id yang baru dibuat
        val orderUpdate = mapOf(
            "chat_id" to chatId
        )

        // Update chat_id di order yang sesuai
        ordersRef.whereEqualTo("order_by", senderId)
            .whereEqualTo("technician_name", receiverId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    ordersRef.document(document.id).update(orderUpdate)
                }
            }
            .addOnFailureListener { e ->
                println("Error updating order with chatId: ${e.message}")
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
