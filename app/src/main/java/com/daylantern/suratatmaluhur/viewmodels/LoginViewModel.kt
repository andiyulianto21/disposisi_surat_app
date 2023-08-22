package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Pegawai
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository) :
    ViewModel() {

    private var _pegawai = MutableLiveData<ResultDataResponse<Pegawai>?>()
    val pegawai: LiveData<ResultDataResponse<Pegawai>?> get() = _pegawai
    
    private var _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> get() = _isLoading
    
    private var _errorMessage = MutableLiveData<ResultResponse?>()
    val errorMessage: LiveData<ResultResponse?> get() = _errorMessage
    
    private var _verifTokenMessage = MutableLiveData<ResultResponse?>()
    val verifTokenMessage: LiveData<ResultResponse?> get() = _verifTokenMessage

    fun login(email: String, password: String, token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = pegawaiRepo.login(email, password, token)
                _pegawai.value = result
            }catch (e: IOException) {
                _errorMessage.value = ResultResponse(500, Constants.ERROR_NO_INTERNET)
            }
            catch (e: Exception){
                _errorMessage.value = ResultResponse(500, e.message ?: "")
            }finally {
                _isLoading.value = false
            }
        }
    }
    
    fun verifikasiToken(id: Int) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.verifikasiToken(id)
                _verifTokenMessage.value = result
            }catch (e: IOException) {
                _errorMessage.value = ResultResponse(500, Constants.ERROR_NO_INTERNET)
            }
        }
    }
}