package com.example.quickcare.model

data class PlaceResponse(
    val results: List<Place>
)

data class Place(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
