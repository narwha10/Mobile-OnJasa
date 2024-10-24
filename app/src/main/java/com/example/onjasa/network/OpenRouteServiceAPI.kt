package com.example.onjasa.network

import com.example.onjasa.models.RouteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenRouteServiceAPI {
    @GET("v2/directions/driving-car")
    fun getDirections(
        @Query("api_key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<RouteResponse>
}
