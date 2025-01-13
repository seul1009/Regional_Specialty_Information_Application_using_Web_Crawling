package com.example.souvenir.retrofit

import com.google.gson.annotations.SerializedName

data class Blog(
    @SerializedName("url") val url: String,
    @SerializedName("name") val name: String,
    @SerializedName("urlImage") val urlImage: String,
    @SerializedName("urlTitle") val urlTitle: String
)