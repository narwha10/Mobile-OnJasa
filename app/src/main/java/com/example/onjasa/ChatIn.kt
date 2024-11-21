package com.example.onjasa

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChatIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_in)

        // Terapkan padding Edge-to-Edge untuk root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tangkap data dari Intent (opsional)
        val userName = intent.getStringExtra("user_name") ?: "Anonymous"
        val technicianName = intent.getStringExtra("technician_name") ?: "Technician"

        // Atur tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }

        // EditText untuk pesan
        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val sendButton = findViewById<ImageView>(R.id.imageView11)

        // LinearLayout untuk menambahkan pesan secara dinamis
        val chatContainer = findViewById<LinearLayout>(R.id.linearLayout9)

        // Tombol kirim pesan
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotBlank()) {
                addMessageToChat(chatContainer, message, isUser = true)
                messageEditText.text.clear()
            }
        }
    }

    /**
     * Fungsi untuk menambahkan pesan ke chat secara dinamis
     */
    private fun addMessageToChat(chatContainer: LinearLayout, message: String, isUser: Boolean) {
        val newMessageView = TextView(this)
        newMessageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            if (isUser) {
                setMargins(120, 16, 16, 16) // Margin untuk pesan pengguna
            } else {
                setMargins(16, 16, 120, 16) // Margin untuk pesan teknisi
            }
        }
        newMessageView.setBackgroundResource(R.drawable.outline_constraint)
        newMessageView.text = message
        newMessageView.textSize = 18f
        newMessageView.setPadding(13, 13, 13, 13)

        // Tambahkan pesan ke chat
        chatContainer.addView(newMessageView)
    }
}
