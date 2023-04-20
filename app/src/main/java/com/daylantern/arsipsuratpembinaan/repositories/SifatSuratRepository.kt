package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.models.SifatModel
import javax.inject.Inject

class SifatSuratRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getSifat(): List<SifatModel> {
        return apiService.getSifat()
    }

    suspend fun addSifat(sifat: String): ResultDataResponse<SifatModel> {
        return apiService.addSifat(sifat)
    }

}