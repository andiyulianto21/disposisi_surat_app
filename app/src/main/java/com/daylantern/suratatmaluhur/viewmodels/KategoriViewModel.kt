package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Kategori
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.KategoriRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class KategoriViewModel @Inject constructor(private val kategoriRepo: KategoriRepository) :
    ViewModel() {
    
    private var _kategori = MutableLiveData<List<Kategori>>()
    val kategori: LiveData<List<Kategori>> get() = _kategori
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    
    private var _operationMessage = MutableLiveData<ResultResponse?>()
    val operationMessage: LiveData<ResultResponse?> get() = _operationMessage
    
    var isToastShown = false
    
    fun getKategori() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = kategoriRepo.getKategori()
                if (result.status == 200) {
                    _kategori.value = result.data
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
    
    fun deleteKategori(kodeKategori: String) {
        viewModelScope.launch {
            try {
                val result = kategoriRepo.deleteKategori(kodeKategori)
                _operationMessage.value = result
                getKategori()
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            } catch (e: Exception) {

            }
        }
    }

    fun updateKategori(kodeLama: String, kodeKategori: String, deskripsi: String) {
        viewModelScope.launch {
            try {
                isToastShown = false
                val result = kategoriRepo.updateKategori(kodeLama, kodeKategori, deskripsi)
                _operationMessage.value = result
                if(result.status == 200) getKategori()
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            } catch (e: Exception) {

            }
        }
    }

    fun addKategori(kodeKategori: String, deskripsi: String) {
        viewModelScope.launch {
            try {
                val result = kategoriRepo.addKategori(kodeKategori, deskripsi)
                _operationMessage.value = ResultResponse(result.status, result.message ?: "")
                if(result.status == 200) getKategori()
                _isLoading.value = false
                _errorMessage.value = null
            }catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
                _isLoading.value = false
            }
        }
    }
    
//
//    fun fetchOneInstansi(idInstansi: Int){
//        viewModelScope.launch {
//            try {
//
//            }
//        }
//    }

}