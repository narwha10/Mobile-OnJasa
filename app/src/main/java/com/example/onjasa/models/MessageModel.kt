package com.example.onjasa.models

data class Message(
    val sender: String,       // Pengirim pesan
    val receiver: String,     // Penerima pesan
    val content: String,      // Isi pesan
    val timestamp: Long       // Waktu pengiriman pesan (epoch time)
)
