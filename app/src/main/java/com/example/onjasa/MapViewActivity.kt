package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapViewActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_view) // Pastikan layout sesuai dengan file XML Anda

        // Konfigurasi MapView
        Configuration.getInstance().userAgentValue = packageName
        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        // Lokasi awal peta (Contoh: koordinat Medan)
        val startPoint = GeoPoint(3.5952, 98.6722)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)

        // Tambahkan marker pada peta
        addMarker(startPoint, "Technician Location")

        // Tombol Kembali
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }

        // Tombol Chat
        val chatButton = findViewById<Button>(R.id.accept_button)
        chatButton.setOnClickListener {
            // Kirim data ke halaman chat
            navigateToChatPage("User123", "Teknisi456")
        }
    }

    /**
     * Menambahkan marker ke MapView
     */
    private fun addMarker(location: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        mapView.overlays.add(marker)
    }

    /**
     * Fungsi untuk navigasi ke halaman ChatActivity
     */
    private fun navigateToChatPage(userName: String, technicianName: String) {
        val intent = Intent(this, ChatIn::class.java)
        intent.putExtra("user_name", userName)
        intent.putExtra("technician_name", technicianName)
        startActivity(intent)
    }
}
