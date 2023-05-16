package com.daylantern.arsipsuratpembinaan.viewmodels

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import com.daylantern.arsipsuratpembinaan.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository) :
    ViewModel() {

    private var _message = MutableLiveData<ResultDataResponse<Pegawai>?>()
    val message: LiveData<ResultDataResponse<Pegawai>?> get() = _message
    
    private var _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> get() = _isLoading

    fun login(nuptk: String, password: String, token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = pegawaiRepo.login(nuptk, password, token)
                _message.value = result
                _isLoading.value = false
            }catch (e: Exception){
//                _message.value = e.message
                _isLoading.value = false
            }
        }
    }
}