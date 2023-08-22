package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.SuratMasuk
import com.daylantern.suratatmaluhur.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailSuratMasukViewModel @Inject constructor(private val suratMasukRepo: SuratMasukRepository) :
    ViewModel() {

    private var _dataSuratMasuk = MutableLiveData<SuratMasuk?>()
    val dataSuratMasuk: LiveData<SuratMasuk?> get() = _dataSuratMasuk

    private var _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> get() = _isLoading

    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchSuratMasuk(idSuratMasuk: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = suratMasukRepo.getSuratMasukById(idSuratMasuk)
                if (result.status == 200) {
                    _dataSuratMasuk.value = result.data
                    _errorMessage.value = null
                    _isLoading.value = false
                }else if(result.status == 404){
                    _errorMessage.value = result.message
                    _isLoading.value = null
                }
            }catch (e: IOException) {
                _errorMessage.value = "Gagal menemukan koneksi internet!\nPastikan Internet atau WI-FI anda nyala"
                _isLoading.value = false
            }
        }
    }

}