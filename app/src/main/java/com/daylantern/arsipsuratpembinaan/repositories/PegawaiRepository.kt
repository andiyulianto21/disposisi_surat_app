package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import retrofit2.http.Path
import javax.inject.Inject

class PegawaiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getPegawai(): List<Pegawai> {
        return apiService.getPegawai()
    }

    suspend fun getLoggedInPegawai(id: Int): ResultDataResponse<Pegawai> {
        return apiService.getLoggedInPegawai(id)
    }
    
    suspend fun verifikasiPassword(id: Int, passwordLama: String): ResultResponse {
        return apiService.verifikasiPassword(id, passwordLama)
    }
    suspend fun ubahPassword(id: Int, passwordBaru: String): ResultResponse {
        return apiService.ubahPassword(id, passwordBaru)
    }

    suspend fun login(nuptk: String, password: String, token: String): ResultDataResponse<Pegawai> {
        return apiService.login(nuptk, password, token)
    }
    
    suspend fun removeTokenNotif(@Path("id") idPegawai: Int): ResultResponse {
        return apiService.removeTokenNotif(idPegawai)
    }

}