package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class Pegawai(
    @SerializedName("id_pegawai") val idPegawai: Int,
    @SerializedName("kode_bagian") val kodeBagian: String,
    @SerializedName("email") val email: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("jabatan") val jabatan: String,
    @SerializedName("level_akses") val levelAkses: String,
    @SerializedName("token_notifikasi") val tokenNotifikasi: String?
)
