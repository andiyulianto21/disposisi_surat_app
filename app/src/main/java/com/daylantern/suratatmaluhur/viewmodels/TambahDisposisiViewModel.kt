package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Pegawai
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import com.daylantern.suratatmaluhur.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TambahDisposisiViewModel @Inject constructor(
    private val suratMasukRepo: SuratMasukRepository,
    private val pegawaiRepo: PegawaiRepository
) : ViewModel() {

    private var _suratMasuk = MutableLiveData<SuratMasuk?>()
    val suratMasuk: LiveData<SuratMasuk?> get() = _suratMasuk

    private var _pihakTujuan = MutableLiveData<MutableList<String>>()
    val pihakTujuan: LiveData<MutableList<String>> get() = _pihakTujuan

    private var _listPegawai = MutableLiveData<List<Pegawai>>()
    val listPegawai: LiveData<List<Pegawai>> get() = _listPegawai

    private val _message = MutableLiveData<ResultResponse?>()
    val message: LiveData<ResultResponse?> get() = _message

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private var _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> get() = _isLoading

    fun getIdPegawai(name: String): Int?{
        return _listPegawai.value?.find { it.nama == name }?.idPegawai
    }
    
    fun clear(){
        _message.value = null
    }

    fun addDisposisi(
        catatanDisposisi: String,
        penerimaDisposisi: String,
        sifatDisposisi: String,
        idPimpinan: Int,
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _message.value = suratMasukRepo.addDisposisi(
                    suratMasuk.value?.idSurat!!,
                    catatanDisposisi,
                    penerimaDisposisi,
                    sifatDisposisi,
                    idPimpinan
                )
            } catch (e: IOException) {
                _message.value = ResultResponse(500, Constants.ERROR_NO_INTERNET)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchSuratMasuk(idSurat: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = suratMasukRepo.getSuratMasukById(idSurat)
                if (result.status == 200) {
                    _suratMasuk.value = result.data
                    _isLoading.value = false
                }else {
//                    _errorMessage.value = result.message
                    _isLoading.value = null
                }
            } catch (e: IOException) {
                _isLoading.value = false
            }
        }
    }

    fun fetchPegawai(excludeIdPegawai : Int) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.getPegawai()
                _listPegawai.value = result.data ?: listOf()
            } catch (e: IOException) {

            }
        }
    }

}