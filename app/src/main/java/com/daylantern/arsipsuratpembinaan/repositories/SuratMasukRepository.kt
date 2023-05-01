package com.daylantern.arsipsuratpembinaan.repositories

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultListDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class SuratMasukRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun addSuratMasuk(
        noSuratParam: String,
        idInstansiParam: String,
        idSifatParam: String,
        perihalParam: String,
        tglSuratMasukParam: String,
        listFile: List<MultipartBody.Part>,
//        file: File,
    ): ResultResponse {
//        val images = mutableListOf<MultipartBody.Part>()
//        listFile.forEach {
//            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
//            val imagePart = MultipartBody.Part.createFormData("images[]", it.name, requestFile)
//            images.add(imagePart)
//        }
//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val imagePart = MultipartBody.Part.createFormData("images", file.name, requestFile)

        val noSurat = noSuratParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idSifat = idSifatParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val idInstansi = idInstansiParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val perihal = perihalParam.toRequestBody("text/plain".toMediaTypeOrNull())
        val tglSuratMasuk = tglSuratMasukParam.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.addSuratMasuk(listFile, noSurat, idInstansi, idSifat, perihal, tglSuratMasuk)
    }

    suspend fun uploadFile(file: List<MultipartBody.Part>): ResultResponse {
        return apiService.upload(file)
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