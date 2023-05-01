package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import javax.inject.Inject

class PegawaiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getPegawai(): List<Pegawai> {
        return apiService.getPegawai()
    }

    suspend fun getLoggedInPegawai(id: Int): ResultDataResponse<Pegawai> {
        return apiService.getLoggedInPegawai(id)
    }

    suspend fun login(nuptk: String, password: String): ResultDataResponse<Pegawai> {
        return apiService.login(nuptk, password)
    }

}