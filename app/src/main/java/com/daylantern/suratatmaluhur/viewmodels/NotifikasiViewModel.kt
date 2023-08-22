package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.JenisSurat
import com.daylantern.suratatmaluhur.entities.Notifikasi
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.NotifikasiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotifikasiViewModel @Inject constructor(private val notifikasiRepo: NotifikasiRepository) :
    ViewModel() {
    
    private var _notifikasi = MutableLiveData<List<Notifikasi>?>()
    val notifikasi: LiveData<List<Notifikasi>?> get() = _notifikasi
    
    private var _idSurat = MutableLiveData<JenisSurat?>()
    val idSurat: LiveData<JenisSurat?> get() = _idSurat
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    
    private var _deleteResult = MutableLiveData<ResultResponse>()
    val deleteResult: LiveData<ResultResponse> get() = _deleteResult
    
    init {
        _notifikasi.value = listOf()
        _idSurat.value = null
    }
    
    fun getNotifikasi(idPegawai: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = notifikasiRepo.getNotifikasi(idPegawai)
                if (result.status == 200) {
                    _notifikasi.value = result.data
                }
                _isLoading.value = false
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
                _isLoading.value = false
            } catch (e: Exception) {
            
            }
        }
    }
    
    fun deleteNotifikasi(idNotifikasi: Int) {
        viewModelScope.launch {
            try{
                _deleteResult.value = notifikasiRepo.deleteNotifikasi(idNotifikasi)
            }catch (e: IOException){}
        }
    }
    fun notifikasiDibaca(idNotifikasi: Int) {
        viewModelScope.launch {
            notifikasiRepo.notifikasiDibaca(idNotifikasi)
        }
    }
}