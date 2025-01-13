package com.example.souvenir.retrofit

// for retrofit
data class HouseModel(
    val id: Int,
    var region: String,
    var name: String,
    val title: String,
    val price: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val imgUrl: String
)

