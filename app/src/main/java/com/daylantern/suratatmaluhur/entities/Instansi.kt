package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class Instansi(
    @SerializedName("id_instansi") val idInstansi: Int,
    @SerializedName("nama") val namaInstansi: String,
    @SerializedName("alamat") val alamatInstansi: String?,
)
