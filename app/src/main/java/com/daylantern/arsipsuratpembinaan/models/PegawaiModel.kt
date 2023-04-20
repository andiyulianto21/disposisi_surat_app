package com.daylantern.arsipsuratpembinaan.models

data class PegawaiModel(
    val id_pegawai: Int,
    val nama: String,
    val password: String?,
    val email: String,
    val jabatan: String
)
