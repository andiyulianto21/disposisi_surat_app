package com.daylantern.suratatmaluhur.repositories

import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.entities.Bagian
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import javax.inject.Inject

class BagianRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getBagian(): ResultListDataResponse<Bagian> { return apiService.getBagian() }
    suspend fun addBagian(kodeBagian: String, deskripsi: String): ResultResponse = apiService.addBagian(kodeBagian, deskripsi)
    suspend fun updateBagian(kodeBagianLama: String, kodeBagian: String, deskripsi: String): ResultResponse =
        apiService.updateBagian(kodeBagianLama, kodeBagian, deskripsi)
    suspend fun deleteBagian(kodeBagian: String): ResultResponse = apiService.deleteBagian(kodeBagian)
}