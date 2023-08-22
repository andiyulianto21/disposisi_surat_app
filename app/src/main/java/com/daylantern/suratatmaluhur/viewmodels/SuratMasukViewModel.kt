package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SuratMasukViewModel @Inject constructor(private val suratMasukRepo: SuratMasukRepository): ViewModel() {

    private var _suratMasuk = MutableLiveData<List<SuratMasuk>>()
    val suratMasuk: LiveData<List<SuratMasuk>> get() = _suratMasuk

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private var _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    
    var isToastShown = false

    init {
        _suratMasuk.value = listOf()
    }
    
    fun getSuratMasuk(idPegawai: Int) {
        viewModelScope.launch {
            try {
                isToastShown = false
                _isLoading.value = true
                _suratMasuk.value = suratMasukRepo.getSuratMasukByIdPegawai(idPegawai).data
            }catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            }catch (e: Exception){
                _errorMessage.value = e.message
            }finally {
                _isLoading.value = false
            }
        }
    }
}