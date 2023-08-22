package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Bagian
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.BagianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BagianViewModel @Inject constructor(private val bagianRepo: BagianRepository) :
    ViewModel() {
    
    private var _bagian = MutableLiveData<List<Bagian>>()
    val bagian: LiveData<List<Bagian>> get() = _bagian
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    
    private var _operationMessage = MutableLiveData<ResultResponse?>()
    val operationMessage: LiveData<ResultResponse?> get() = _operationMessage
    
    var isToastShown = false
    
    fun getBagian() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = bagianRepo.getBagian()
                if (result.status == 200) {
                    _bagian.value = result.data
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
    
    fun deleteBagian(kodeBagian: String) {
        viewModelScope.launch {
            try {
                val result = bagianRepo.deleteBagian(kodeBagian)
                _operationMessage.value = result
                getBagian()
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            } catch (e: Exception) {

            }
        }
    }

    fun updateBagian(kodeLama: String, kodeBagian: String, deskripsi: String) {
        viewModelScope.launch {
            try {
                isToastShown = false
                val result = bagianRepo.updateBagian(kodeLama, kodeBagian, deskripsi)
                _operationMessage.value = result
                if(result.status == 200) getBagian()
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            } catch (e: Exception) {

            }
        }
    }

    fun addBagian(kodeBagian: String, deskripsi: String) {
        viewModelScope.launch {
            try {
                val result = bagianRepo.addBagian(kodeBagian, deskripsi)
                _operationMessage.value = ResultResponse(result.status, result.message ?: "")
                if(result.status == 200) getBagian()
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