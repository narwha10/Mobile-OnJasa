package com.example.onjasa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.models.GeocodingResult
import com.example.onjasa.network.NominatimAPI
import com.google.firebase.firestore.DocumentSnapshot
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
    private lateinit var addressTextView: TextView
    private lateinit var finishOrderButton: Button

    private var startLatitude: Double = 3.5833 // Lokasi mulai (contoh: Medan)
    private var startLongitude: Double = 98.6667 // Lokasi mulai (contoh: Medan)

    private var endLatitude: Double? = null
    private var endLongitude: Double? = null
    private var case1Snapshot: DocumentSnapshot? = null // Menyimpan snapshot dokumen untuk case 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_view)

        // Inisialisasi UI
        mapView = findViewById(R.id.mapView)
        namaTeknisiTextView = findViewById(R.id.namaTeknisiTextView)
        hargaTeknisiTextView = findViewById(R.id.hargaTeknisiTextView)
        addressTextView = findViewById(R.id.address)
        finishOrderButton = findViewById(R.id.finish_order_button)

        // Konfigurasi MapView
        Configuration.getInstance().userAgentValue = packageName
        mapView.setMultiTouchControls(true)

        // Tambahkan marker lokasi mulai
        addMarker(startLatitude, startLongitude, "Lokasi Mulai")

        // Ambil username dari Intent
        val username = intent.getStringExtra("USERNAME") ?: ""
        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set onClick listener untuk tombol Finish Order
        finishOrderButton.setOnClickListener {
            updateOrderStatusToDone(username)
        }

        // Mulai proses pengambilan data dari Firestore
        checkUsernameInOrders(username)
    }

    private fun checkUsernameInOrders(username: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("orders")
            .whereEqualTo("order_by", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    case1Snapshot = document // Simpan snapshot untuk case 1
                    val technicianName = document.getString("technician_name")
                    val technicianPrice = document.getString("technician_price")
                    val address = document.getString("alamat")

                    if (!technicianName.isNullOrEmpty() && !technicianPrice.isNullOrEmpty() && !address.isNullOrEmpty()) {
                        updateTechnicianInfo(technicianName, technicianPrice, address)
                    } else {
                        Toast.makeText(this, "Field tidak lengkap dalam dokumen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Jika tidak cocok dengan "order_by", cek "technician_name"
                    checkTechnicianForOrder(username)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapViewActivity", "Error saat memeriksa field order_by: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkTechnicianForOrder(username: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("orders")
            .whereEqualTo("technician_name", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val technicianName = document.getString("technician_name")
                    val technicianPrice = document.getString("technician_price")
                    val address = document.getString("alamat")

                    if (!technicianName.isNullOrEmpty() && !technicianPrice.isNullOrEmpty() && !address.isNullOrEmpty()) {
                        // Case 2: Tampilkan tombol Finish Order
                        finishOrderButton.visibility = View.VISIBLE
                        finishOrderButton.tag = document.id // Simpan ID dokumen
                        updateTechnicianInfo(technicianName, technicianPrice, address)
                    } else {
                        Toast.makeText(this, "Field tidak lengkap dalam dokumen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Username tidak ditemukan di order_by maupun technician_name", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapViewActivity", "Error saat memeriksa field technician_name: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTechnicianInfo(technicianName: String, technicianPrice: String, address: String) {
        // Update UI dengan data yang diambil
        namaTeknisiTextView.text = technicianName
        hargaTeknisiTextView.text = technicianPrice
        addressTextView.text = address

        // Dapatkan koordinat dari alamat
        getCoordinatesFromAddress(address)
    }

    private fun updateOrderStatusToDone(username: String) {
        val db = FirebaseFirestore.getInstance()
        val documentId = finishOrderButton.tag as? String

        if (documentId != null) {
            db.collection("orders").document(documentId)
                .update("status", "done")
                .addOnSuccessListener {
                    Toast.makeText(this, "Order berhasil diselesaikan", Toast.LENGTH_SHORT).show()
                    finishOrderButton.visibility = View.GONE // Sembunyikan tombol setelah selesai

                    // Intent ke HomeActivity atau HomeTechActivity sesuai case
                    if (case1Snapshot != null) {
                        // Case 1: Intent ke HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("order_by", username)
                        startActivity(intent)
                    } else {
                        // Case 2: Intent ke HomeTechActivity
                        val intent = Intent(this, HomeTechActivity::class.java)
                        intent.putExtra("technician_name", username)
                        startActivity(intent)
                    }
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("MapViewActivity", "Error saat memperbarui status order: ${e.message}")
                    Toast.makeText(this, "Gagal menyelesaikan order: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Gagal mendapatkan ID dokumen order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCoordinatesFromAddress(address: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
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
                            "Lokasi tujuan: $endLatitude, $endLongitude",
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
                Toast.makeText(this@MapViewActivity, "Error API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.title = title
        mapView.overlays.add(marker)
        mapView.controller.setCenter(marker.position)
        mapView.controller.setZoom(15.0)
    }
}
