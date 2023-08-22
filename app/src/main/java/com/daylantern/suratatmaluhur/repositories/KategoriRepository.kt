package com.daylantern.suratatmaluhur.repositories

import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.entities.Kategori
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import javax.inject.Inject

class KategoriRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getKategori(): ResultListDataResponse<Kategori> =  apiService.getKategori()
    suspend fun addKategori(kodeKategori: String, deskripsi: String): ResultResponse = apiService.addKategori(kodeKategori, deskripsi)
    suspend fun updateKategori(kodeKategoriLama: String, kodeKategori: String, deskripsi: String): ResultResponse =
        apiService.updateKategori(kodeKategoriLama, kodeKategori, deskripsi)
    suspend fun deleteKategori(kodeKategori: String): ResultResponse = apiService.deleteKategori(kodeKategori)
}