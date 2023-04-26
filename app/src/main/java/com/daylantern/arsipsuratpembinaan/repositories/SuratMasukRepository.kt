package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultListDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
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

    suspend fun getSuratMasuk(): ResultListDataResponse<SuratMasuk>{
        return apiService.getSuratMasuk()
    }

    suspend fun getSuratMasukById(idSuratMasuk: Int): ResultDataResponse<SuratMasuk> {
        return apiService.getSuratMasukById(idSuratMasuk)
    }

    suspend fun addDisposisi(
        idSuratMasuk: String,
        idDisposisi: String,
        catatanDisposisi: String,
        pihakTujuan: String,
    ): ResultResponse {
        return apiService.addDisposisi(idSuratMasuk, idDisposisi, catatanDisposisi, pihakTujuan)
    }

}