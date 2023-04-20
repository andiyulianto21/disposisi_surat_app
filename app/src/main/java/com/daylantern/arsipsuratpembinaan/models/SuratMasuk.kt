package com.daylantern.arsipsuratpembinaan.models

import retrofit2.http.Field

data class SuratMasukModel(
    val noSurat: String,
    val idInstansi: Int,
    val idSifat: Int,
    val perihal: String,
    val tglSuratMasuk: String,

)
