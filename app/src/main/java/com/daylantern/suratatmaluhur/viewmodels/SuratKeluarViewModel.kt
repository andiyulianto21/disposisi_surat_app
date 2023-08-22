package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.SuratKeluar
import com.daylantern.suratatmaluhur.repositories.SuratKeluarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SuratKeluarViewModel @Inject constructor(private val suratKeluarRepo: SuratKeluarRepository): ViewModel() {
    
    private var _result = MutableLiveData<ResultListDataResponse<SuratKeluar>?>()
    val result: LiveData<ResultListDataResponse<SuratKeluar>?> get() = _result
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    fun getSuratKeluar(idPegawai: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = suratKeluarRepo.getSuratKeluarByIdPegawai(idPegawai)
                _result.value = result
                _isLoading.value = false
            }catch (e: IOException){}
        }
    }
    
}