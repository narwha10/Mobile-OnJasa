package com.example.onjasa.models

import org.osmdroid.util.GeoPoint

data class RouteResponse(
    val features: List<Feature>
) {
    fun getCoordinates(): List<GeoPoint> {
        val coordinates = features[0].geometry.coordinates
        return coordinates.map { GeoPoint(it[1], it[0]) }
    }

    data class Feature(val geometry: Geometry)
    data class Geometry(val coordinates: List<List<Double>>)
}
