package com.daylantern.suratatmaluhur.entities

import com.daylantern.suratatmaluhur.models.enums.StatusSuratMasuk
import com.google.gson.annotations.SerializedName

data class JenisSurat(
    @SerializedName("id_surat_masuk") val idSurat: Int,
    @SerializedName("status_surat") val statusSurat: StatusSuratMasuk,
)
