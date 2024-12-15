package com.example.quickcare.api

import com.example.quickcare.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApiService {
    @GET("place/nearbysearch/json")
    fun getNearbyHospitals(
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "hospital",
        @Query("key") apiKey: String
    ): Call<PlaceResponse>
}
