package com.daylantern.suratatmaluhur.repositories

import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.entities.Surat
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class SuratMasukRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun getLaporanSuratMasuk(
        dariTgl: String,
        sampaiTgl: String,
    ): ResultListDataResponse<SuratMasuk>{
        return apiService.getLaporanSuratMasuk(dariTgl, sampaiTgl)
    }
    
    suspend fun addSuratMasuk(
        noSuratParam: String,
        idInstansiParam: String,
        idPenginputParam: String,
        kodeKategoriParam: String,
        perihalParam: String,
        tglSuratMasukParam: String,
        listFile: List<MultipartBody.Part>,
    ): ResultDataResponse<Int> {
        val noSurat = noSuratParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idInstansi = idInstansiParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idPenginput = idPenginputParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val kodeKategori = kodeKategoriParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val perihal = perihalParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val tglSuratMasuk = tglSuratMasukParam.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.addSuratMasuk(listFile, noSurat, idInstansi, idPenginput, kodeKategori, perihal, tglSuratMasuk)
    }

    suspend fun getSuratMasukById(idSuratMasuk: Int): ResultDataResponse<SuratMasuk> {
        return apiService.getSuratMasukById(idSuratMasuk)
    }
    
    suspend fun getSuratMasukByIdPegawai(idPegawai: Int): ResultListDataResponse<SuratMasuk> {
        return apiService.getSuratMasukByIdPegawai(idPegawai)
    }

    suspend fun addDisposisi(
        idSurat: String,
        catatanDisposisi: String,
        penerimaDisposisi: String,
        sifatDisposisi: String,
        idPimpinan: Int,
    ): ResultResponse {
        return apiService.addDisposisi(idSurat, catatanDisposisi, penerimaDisposisi, sifatDisposisi, idPimpinan)
    }
    
    suspend fun getDisposisi(): ResultListDataResponse<SuratMasuk> = apiService.getDisposisi()
    suspend fun cariSurat(keyword: String, idPencari: Int, jenisSurat: String): ResultListDataResponse<Surat> {
        return apiService.cariSurat(keyword, idPencari, jenisSurat)
    }

}