package com.daylantern.arsipsuratpembinaan.repositories

import com.daylantern.arsipsuratpembinaan.ApiService
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.entities.Instansi
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import javax.inject.Inject

class InstansiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getInstansi(idInstansi: Int ?= null): ResultDataResponse<List<Instansi>> {
        if(idInstansi != null)
            return apiService.getInstansiById(idInstansi)
        return apiService.getInstansi()
    }

    suspend fun addInstansi(nama: String, alamat: String): ResultDataResponse<Instansi> {
        return apiService.addInstansi(nama, alamat)
    }
    
    suspend fun deleteInstansi(idInstansi: Int): ResultResponse {
        return apiService.deleteInstansi(idInstansi)
    }
    
    suspend fun updateInstansi(idInstansi: Int, namaInstansi: String, alamatInstansi: String): ResultResponse {
        return apiService.updateInstansi(idInstansi, namaInstansi, alamatInstansi)
    }
}