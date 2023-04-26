package com.daylantern.arsipsuratpembinaan.models

data class SuratMasukModel(
    val noSurat: String,
    val idInstansi: Int,
    val idSifat: Int,
    val perihal: String,
    val tglSuratMasuk: String,

)
