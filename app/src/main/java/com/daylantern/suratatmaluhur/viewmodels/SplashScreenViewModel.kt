package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository): ViewModel() {
    
    private var _hasToken = MutableLiveData<ResultResponse?>()
    val hasToken: LiveData<ResultResponse?> get() = _hasToken
    private var _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message
    
    fun verifikasiToken(id: Int) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.verifikasiToken(id)
                _hasToken.value = result
                _message.value = null
            }catch (e: IOException) {
                _message.value = Constants.ERROR_NO_INTERNET
            }
        }
    }
    
}