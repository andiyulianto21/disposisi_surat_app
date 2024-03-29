package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Instansi
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.InstansiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class InstansiViewModel @Inject constructor(private val instansiRepo: InstansiRepository) :
    ViewModel() {
    
    private var _instansi = MutableLiveData<List<Instansi>?>()
    val instansi: LiveData<List<Instansi>?> get() = _instansi
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    
    private var _operationMessage = MutableLiveData<ResultResponse?>()
    val operationMessage: LiveData<ResultResponse?> get() = _operationMessage
    
    var isToastShown = false
    
    fun fetchInstansi() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = instansiRepo.getInstansi()
                if (result.status == 200) {
                    _instansi.value = result.data
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
    
    fun deleteInstansi(idInstansi: Int) {
        viewModelScope.launch {
            try {
                val result = instansiRepo.deleteInstansi(idInstansi)
                _operationMessage.value = result
                fetchInstansi()
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            } catch (e: Exception) {
    
            }
        }
    }
    
    fun updateInstansi(idInstansi: Int, namaInstansi: String, alamatInstansi: String) {
        viewModelScope.launch {
            try {
                isToastShown = false
                val result = instansiRepo.updateInstansi(idInstansi, namaInstansi, alamatInstansi)
                _operationMessage.value = result
                if(result.status == 200) fetchInstansi()
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            } catch (e: Exception) {
    
            }
        }
    }
    
    fun addInstansi(namaInstansi: String, alamatInstansi: String) {
        viewModelScope.launch {
            try {
                val result = instansiRepo.addInstansi(namaInstansi, alamatInstansi)
                _operationMessage.value = ResultResponse(result.status, result.message ?: "")
                if(result.status == 200)
                    fetchInstansi()
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