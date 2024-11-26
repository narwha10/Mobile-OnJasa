package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.models.GeocodingResult
import com.example.onjasa.network.NominatimAPI
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapViewActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var namaTeknisiTextView: TextView
    private lateinit var hargaTeknisiTextView: TextView

    private var startLatitude: Double = 3.5833 // Contoh lokasi Medan
    private var startLongitude: Double = 98.6667 // Contoh lokasi Medan
    private var endLatitude: Double? = null
    private var endLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_view)

        // Inisialisasi MapView
        Configuration.getInstance().userAgentValue = packageName
        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        // Ambil username, technicianName, dan technicianPrice dari Intent
        val username = intent.getStringExtra("USERNAME") ?: ""
        val technicianName = intent.getStringExtra("TECHNICIAN_NAME") ?: ""
        val technicianPrice = intent.getStringExtra("TECHNICIAN_PRICE") ?: ""

        // Inisialisasi TextView
        namaTeknisiTextView = findViewById(R.id.namaTeknisiTextView)
        hargaTeknisiTextView = findViewById(R.id.hargaTeknisiTextView)

        // Cek dan tampilkan nama teknisi dan harga teknisi

        namaTeknisiTextView.text = technicianName // Set nama teknisi
        hargaTeknisiTextView.text = technicianPrice // Set harga teknisi


        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Tambahkan marker lokasi mulai
        Log.d("MapViewActivity", "Menambahkan marker lokasi mulai: $startLatitude, $startLongitude")
        addMarker(startLatitude, startLongitude, "Lokasi Mulai")

        // Ambil alamat dari Firestore berdasarkan field "order_by"
        getEndAddressFromFirebase(username)

        // Set up tombol accept_button untuk membuka ChatInActivity
        val acceptButton = findViewById<Button>(R.id.accept_button)
        acceptButton.setOnClickListener {
            // Membuat intent untuk berpindah ke ChatInActivity
            val intent = Intent(this, ChatIn::class.java).apply {
                putExtra("USERNAME", username)         // Kirimkan username
                putExtra("TECHNICIAN_NAME", technicianName)  // Kirimkan technicianName
                putExtra("TECHNICIAN_PRICE", technicianPrice) // Kirimkan technicianPrice
            }
            startActivity(intent) // Menjalankan Activity
        }
    }

    private fun getEndAddressFromFirebase(username: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .whereEqualTo("order_by", username) // Periksa berdasarkan field "order_by"
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val alamat = documents.documents[0].getString("alamat")
                    if (!alamat.isNullOrEmpty()) {
                        Log.d("MapViewActivity", "Alamat ditemukan: $alamat")
                        getCoordinatesFromAddress(alamat)
                    } else {
                        Toast.makeText(this, "Field alamat tidak ditemukan pada dokumen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Dokumen tidak ditemukan untuk username: $username", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapViewActivity", "Error mendapatkan data Firestore: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCoordinatesFromAddress(address: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/") // Geocoding API
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "OnJasa/1.0 (mhfadtz@gmail.com)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        val api = retrofit.create(NominatimAPI::class.java)
        val call = api.getCoordinates(address)

        call.enqueue(object : Callback<List<GeocodingResult>> {
            override fun onResponse(
                call: Call<List<GeocodingResult>>,
                response: Response<List<GeocodingResult>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val geocodingResults = response.body()
                    if (!geocodingResults.isNullOrEmpty()) {
                        val result = geocodingResults[0]
                        endLatitude = result.lat.toDouble()
                        endLongitude = result.lon.toDouble()

                        Toast.makeText(
                            this@MapViewActivity,
                            "Lokasi tujuan: ${endLatitude}, ${endLongitude}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Tambahkan marker untuk lokasi tujuan
                        if (endLatitude != null && endLongitude != null) {
                            addMarker(endLatitude!!, endLongitude!!, "Lokasi Tujuan")
                        }
                    } else {
                        Toast.makeText(this@MapViewActivity, "Gagal mendapatkan koordinat tujuan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MapViewActivity, "Gagal mendapatkan respons geocoding", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<GeocodingResult>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@MapViewActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        Log.d("MapViewActivity", "Menambahkan marker: $title, Latitude: $latitude, Longitude: $longitude")

        // Tambahkan marker baru
        val marker = Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        // Perbarui tampilan peta
        mapView.invalidate()

        // Pusatkan peta hanya untuk marker pertama (Lokasi Mulai)
        if (title == "Lokasi Mulai") {
            mapView.controller.apply {
                setZoom(15.0)
                setCenter(GeoPoint(latitude, longitude))
            }
        }
    }
}



