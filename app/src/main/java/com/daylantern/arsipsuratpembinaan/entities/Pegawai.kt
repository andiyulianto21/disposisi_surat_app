package com.daylantern.arsipsuratpembinaan.entities

import com.google.gson.annotations.SerializedName

data class Pegawai(
    @SerializedName("id_pegawai") val idPegawai: Int,
    @SerializedName("nuptk") val nuptk: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("jabatan") val jabatan: String,
    @SerializedName("foto") val foto: String?,
)
