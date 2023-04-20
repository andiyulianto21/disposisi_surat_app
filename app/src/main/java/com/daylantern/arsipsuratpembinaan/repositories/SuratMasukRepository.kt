package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import javax.inject.Inject

class SuratMasukRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun addSuratMasuk(
        noSurat: String,
        idInstansi: String,
        idSifat: String,
        perihal: String,
        tglSuratMasuk: String
    ): ResultResponse {
        return apiService.addSuratMasuk(noSurat, idInstansi, idSifat, perihal, tglSuratMasuk)
    }

}