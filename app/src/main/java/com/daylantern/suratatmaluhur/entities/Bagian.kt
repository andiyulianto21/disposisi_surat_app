package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class Bagian(
    @SerializedName("kode_bagian") val kodeBagian: String,
    @SerializedName("deskripsi") val deskripsi: String
)
