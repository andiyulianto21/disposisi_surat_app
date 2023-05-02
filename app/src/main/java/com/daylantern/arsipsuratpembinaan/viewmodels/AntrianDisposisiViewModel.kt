package com.daylantern.arsipsuratpembinaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AntrianDisposisiViewModel @Inject constructor(private val suratMasukRepo: SuratMasukRepository): ViewModel() {

    private var _antrianDisposisi = MutableLiveData<List<SuratMasuk>>()
    val antrianDisposisi: LiveData<List<SuratMasuk>> get() = _antrianDisposisi

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchAntrianDisposisi() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = suratMasukRepo.getDisposisi()
                if(result.status == 200){
                    _antrianDisposisi.postValue(result.data)
                }
                _isLoading.value = false
                _errorMessage.value = null
            }catch (e: IOException) {
                _errorMessage.value = "Gagal menemukan koneksi internet!\nPastikan Internet atau WI-FI anda nyala"
                _isLoading.value = false
            }catch (e: Exception){

            }
        }
    }

}