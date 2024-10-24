package com.example.onjasa

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.onjasa.network.OpenRouteServiceAPI
import com.example.onjasa.models.RouteResponse

class MapViewActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var motorMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.map_view)

        mapView = findViewById(R.id.map)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(3.5951956, 98.6722227)) // Pusat Medan

        motorMarker = Marker(mapView).apply {
            icon = resizeDrawable(R.drawable.motorcycle , 64, 64) // Ikon motor
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        mapView.overlays.add(motorMarker)

        fetchRoute() // Panggil API untuk rute
    }
    private fun resizeDrawable(drawableRes: Int, width: Int, height: Int): Drawable {
        val bitmap = BitmapFactory.decodeResource(resources, drawableRes)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDrawable(resources, resizedBitmap)
    }
    private fun fetchRoute() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(OpenRouteServiceAPI::class.java)
        val apiKey = "5b3ce3597851110001cf6248e187f81a49d941f5936e70bd593aea33"

        val call = api.getDirections(
            apiKey,
            "98.671722,3.595291", // Mulai
            "98.676500,3.583979"  // Tujuan
        )

        call.enqueue(object : Callback<RouteResponse?> {
            override fun onResponse(call: Call<RouteResponse?>, response: Response<RouteResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val coordinates = response.body()!!.getCoordinates()
                    drawRoute(coordinates)
                    animateMotorAlongRoute(coordinates)
                }
            }

            override fun onFailure(call: Call<RouteResponse?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun drawRoute(coordinates: List<GeoPoint>) {
        val polyline = Polyline().apply {
            setPoints(coordinates)
            color = -0xff6634 // Warna biru tebal
            width = 10.0f
        }
        mapView.overlays.add(polyline)
        mapView.invalidate() // Refresh peta
    }

    private fun animateMotorAlongRoute(coordinates: List<GeoPoint>) {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 10000 // 10 detik (sesuaikan durasinya)
            addUpdateListener { animation ->
                val fraction = animation.animatedFraction
                val index = (fraction * (coordinates.size - 1)).toInt()
                val point = coordinates[index]

                motorMarker.position = point
                mapView.controller.setCenter(point)
                mapView.invalidate()
            }
        }
        animator.start()
    }
}
