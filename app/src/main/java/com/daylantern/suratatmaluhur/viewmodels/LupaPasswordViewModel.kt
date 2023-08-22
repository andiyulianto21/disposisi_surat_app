package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LupaPasswordViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository): ViewModel(){
    
    private var _result = MutableLiveData<ResultDataResponse<String>?>()
    val result: MutableLiveData<ResultDataResponse<String>?> get() = _result
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    fun clear(){
        _result.value = null
    }
    
    fun kirimEmail(email: String){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _result.value = pegawaiRepo.lupaPassword(email)
            }catch (e: IOException){
            
            }
            finally {
                _isLoading.value = false
            }
        }
    }
    
}