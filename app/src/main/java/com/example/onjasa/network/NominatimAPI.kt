package com.example.onjasa.network

import com.example.onjasa.models.GeocodingResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimAPI {
    @GET("search")
    fun getCoordinates(
        @Query("q") address: String,
        @Query("format") format: String = "json",
        @Query("countrycodes") countryCodes: String = "id" // Contoh: membatasi pencarian untuk Indonesia
    ): Call<List<GeocodingResult>>
}
