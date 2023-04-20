package com.daylantern.arsipsuratpembinaan.models

import com.google.gson.annotations.SerializedName

data class InstansiModel(
    @SerializedName("id_instansi") val idInstansi: String,
    @SerializedName("nama_instansi") val namaInstansi: String,
    @SerializedName("alamat_instansi") val alamatInstansi: String,
)
