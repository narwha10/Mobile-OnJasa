package com.example.onjasa.models

data class GeocodingResult(
    val lat: String,
    val lon: String,
    val display_name: String? = null // Menyimpan nama lokasi jika dibutuhkan
)
