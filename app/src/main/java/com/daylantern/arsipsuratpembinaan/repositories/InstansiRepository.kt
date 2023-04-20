package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.models.InstansiModel
import javax.inject.Inject

class InstansiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getInstansi(): List<InstansiModel> {
        return apiService.getInstansi()
    }

    suspend fun addInstansi(nama: String, alamat: String): ResultDataResponse<InstansiModel> {
        return apiService.addInstansi(nama, alamat)
    }

}