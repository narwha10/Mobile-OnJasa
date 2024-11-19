package com.example.onjasa

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onjasa.models.GeocodingResult
import com.example.onjasa.network.NominatimAPI
import okhttp3.OkHttpClient
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapViewActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    // Menyimpan koordinat lokasi mulai dan tujuan
    private var startLatitude: Double? = null
    private var startLongitude: Double? = null
    private var endLatitude: Double? = null
    private var endLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_view)

        // Inisialisasi MapView
        Configuration.getInstance().userAgentValue = packageName
        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        // Panggil fungsi untuk mendapatkan koordinat untuk lokasi mulai dan tujuan
        getCoordinatesFromAddress("Medan")  // Gantilah dengan alamat lokasi mulai
        getCoordinatesFromAddress("Jalan Walikota")  // Gantilah dengan alamat tujuan
    }

    private fun getCoordinatesFromAddress(address: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "OnJasa/1.0 (mhfadtz@gmail.com)") // Ganti sesuai kebutuhan
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
                    // Log hasil JSON untuk pemeriksaan
                    val geocodingResults = response.body()
                    for (result in geocodingResults!!) {
                        Log.d("Geocoding", "Koordinat: Lat ${result.lat}, Lon ${result.lon}")
                    }

                    val geocodingResult = geocodingResults[0]
                    val lat = geocodingResult.lat.toDouble()
                    val lon = geocodingResult.lon.toDouble()

                    // Menyimpan koordinat sesuai dengan alamat
                    if (startLatitude == null && startLongitude == null) {
                        startLatitude = lat
                        startLongitude = lon
                        Toast.makeText(
                            this@MapViewActivity,
                            "Lokasi Mulai: $lat, $lon",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        endLatitude = lat
                        endLongitude = lon
                        Toast.makeText(
                            this@MapViewActivity,
                            "Lokasi Tujuan: $lat, $lon",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // Setelah mendapatkan kedua koordinat, tambahkan marker
                    if (startLatitude != null && startLongitude != null && endLatitude != null && endLongitude != null) {
                        addMarker(startLatitude!!, startLongitude!!, "Lokasi Mulai")
                        addMarker(endLatitude!!, endLongitude!!, "Lokasi Tujuan")
                    }
                } else {
                    Toast.makeText(this@MapViewActivity, "Gagal mendapatkan koordinat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<GeocodingResult>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@MapViewActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String) {
        // Pusatkan peta ke lokasi
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(org.osmdroid.util.GeoPoint(latitude, longitude))

        // Tambahkan marker
        val marker = Marker(mapView)
        marker.position = org.osmdroid.util.GeoPoint(latitude, longitude)
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        // Perbarui tampilan peta
        mapView.invalidate()
    }
}
