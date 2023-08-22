package com.daylantern.suratatmaluhur.repositories

import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.entities.SuratKeluar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class SuratKeluarRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun createKonsepSurat(
        perihalParam: String,
        isiSuratParam: String,
        idPembuatParam: String,
        idPenandatanganParam: String,
        idInstansiTujuanParam: String,
        namaTujuanParam: String,
        jabatanTujuanParam: String,
        kodeKategoriParam: String,
        tembusanParam: String,
        lampiranFiles: List<MultipartBody.Part>
    ): ResultDataResponse<Int> {
        val perihal = perihalParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val isiSurat = isiSuratParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idPembuat = idPembuatParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idPenandatangan = idPenandatanganParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idInstansiTujuan = idInstansiTujuanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val namaTujuan = namaTujuanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val jabatanTujuan = jabatanTujuanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val kodeKategori = kodeKategoriParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val tembusan = tembusanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.createKonsepSurat(
            perihal,
            isiSurat,
            idPembuat,
            idPenandatangan,
            idInstansiTujuan,
            namaTujuan,
            jabatanTujuan,
            kodeKategori,
            tembusan,
            lampiranFiles
        )
    }
    
    suspend fun editSurat(
        idSurat: Int,
        perihalParam: String,
        isiSuratParam: String,
        idInstansiTujuanParam: String,
        namaTujuanParam: String,
        jabatanTujuanParam: String,
        kodeKategoriParam: String,
        tembusanParam: String,
//        lampiranFiles: List<MultipartBody.Part>
    ): ResultDataResponse<Int> {
        val perihal = perihalParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val isiSurat = isiSuratParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idInstansiTujuan = idInstansiTujuanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val namaTujuan = namaTujuanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val jabatanTujuan = jabatanTujuanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val kodeKategori = kodeKategoriParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val tembusan = tembusanParam.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.editSurat(
            idSurat,
            perihal,
            isiSurat,
            idInstansiTujuan,
            namaTujuan,
            jabatanTujuan,
            kodeKategori,
            tembusan
//            lampiranFiles
        )
    }
    
    suspend fun reviewKonsepSurat(
        idSurat: Int,
        instruksiPenandatangan: String,
        statusSurat: String
    ): ResultResponse = apiService.reviewKonsepSurat(idSurat, instruksiPenandatangan, statusSurat)
    
    suspend fun getSuratKeluarByIdSurat(idSurat: Int): ResultDataResponse<SuratKeluar> =
        apiService.getSuratKeluarByIdSurat(idSurat)
    
    suspend fun getSuratKeluarByIdPegawai(idPegawai: Int): ResultListDataResponse<SuratKeluar> =
        apiService.getSuratKeluarByIdPegawai(idPegawai)
}