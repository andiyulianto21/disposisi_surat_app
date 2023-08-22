package com.daylantern.suratatmaluhur.repositories

import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.entities.Notifikasi
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import javax.inject.Inject

class NotifikasiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getNotifikasi(idPegawai: Int): ResultListDataResponse<Notifikasi> = apiService.getNotifikasi(idPegawai)
    suspend fun notifikasiUnread(idPegawai: Int): Boolean = apiService.notifikasiUnread(idPegawai)
    suspend fun notifikasiDibaca(idNotifikasi: Int): Boolean = apiService.notifikasiDibaca(idNotifikasi)
    suspend fun deleteNotifikasi(idNotifikasi: Int) = apiService.deleteNotifikasi(idNotifikasi)
}