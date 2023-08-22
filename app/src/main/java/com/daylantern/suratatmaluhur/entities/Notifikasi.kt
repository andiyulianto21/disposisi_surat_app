package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class Notifikasi(
    @SerializedName("id_notifikasi") val idNotifikasi: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("status_dibaca") val statusDibaca: String,
    @SerializedName("id_surat") val idSurat: Int,
    @SerializedName("no_surat") val noSurat: String?,
    @SerializedName("tgl_notifikasi") val tglNotifikasi: String,
    @SerializedName("status_surat") val statusSurat: String,
    @SerializedName("jenis_surat") val jenisSurat: String
)
