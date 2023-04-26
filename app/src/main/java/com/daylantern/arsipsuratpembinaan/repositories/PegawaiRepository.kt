package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import javax.inject.Inject

class PegawaiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getPegawai(): List<Pegawai> {
        return apiService.getPegawai()
    }

}