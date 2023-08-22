package com.daylantern.suratatmaluhur.repositories

import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.entities.Pegawai
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class PegawaiRepository @Inject constructor(private val apiService: ApiService) {
    
    suspend fun getPegawai(): ResultListDataResponse<Pegawai> {
        return apiService.getPegawai()
    }
    
    suspend fun getLoggedInPegawai(id: Int): ResultDataResponse<Pegawai> {
        return apiService.getLoggedInPegawai(id)
    }
    
    suspend fun verifikasiToken(id: Int): ResultResponse {
        return apiService.verifikasiToken(id)
    }
    
    suspend fun ubahPassword(
        id: Int,
        passwordSekarang: String,
        passwordBaru: String
    ): ResultResponse {
        return apiService.ubahPassword(id, passwordSekarang, passwordBaru)
    }
    
    suspend fun register(
        email: String,
        password: String,
        kodeBagian: String,
        nama: String,
        jabatan: String,
        levelAkses: String
    ): ResultResponse {
        return apiService.register(email, password, kodeBagian, nama, jabatan, levelAkses)
    }
    
    suspend fun login(email: String, password: String, token: String): ResultDataResponse<Pegawai> =
        apiService.login(email, password, token)
    
    suspend fun lupaPassword(email: String): ResultDataResponse<String> =
        apiService.lupaPassword(email)
    
    suspend fun resetPassword(email: String, password: String, token: String): ResultResponse =
        apiService.resetPassword(email, password, token)
    
    suspend fun deleteTokenNotif(idPegawai: Int, tokenFcm: String): ResultResponse =
        apiService.deleteTokenNotif(idPegawai, tokenFcm)
    
    suspend fun deletePegawai(idPegawai: Int): ResultResponse = apiService.deletePegawai(idPegawai)
    suspend fun updatePegawai(
        idPegawai: Int,
        nama: String,
        kodeBagian: String,
        email: String,
        levelAkses: String,
        jabatan: String,
        password: String?,
    ): ResultResponse = apiService.updatePegawai(idPegawai, nama, kodeBagian, email, levelAkses, jabatan, password)
    
    suspend fun addTandaTangan(file: MultipartBody.Part): ResultResponse =
        apiService.addTandaTangan(file)
}