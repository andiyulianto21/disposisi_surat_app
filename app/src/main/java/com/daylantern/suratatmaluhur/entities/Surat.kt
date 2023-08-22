package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class Surat (
    @SerializedName("id_surat") val idSurat: String,
    @SerializedName("no_surat") val noSurat: String? = null,
    @SerializedName("tgl_surat") val tglSurat: String,
    @SerializedName("instansi" ) val instansi : String,
    @SerializedName("perihal") val perihal: String,
    @SerializedName("status_surat") val statusSurat: String,
    @SerializedName("sifat_disposisi") val sifatDisposisi: String? = null,
)