package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class Kategori(
    @SerializedName("kode_kategori") val kodeKategori: String,
    @SerializedName("deskripsi") val deskripsi: String,
)
