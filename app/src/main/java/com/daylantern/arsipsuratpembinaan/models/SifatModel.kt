package com.daylantern.arsipsuratpembinaan.models

import com.google.gson.annotations.SerializedName

data class SifatModel(
    @SerializedName("id_sifat") val idSifat: String,
    @SerializedName("sifat_surat") val sifatSurat: String,
)
