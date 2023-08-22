package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.entities.SuratKeluar
import com.daylantern.suratatmaluhur.repositories.SuratKeluarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailSuratKeluarViewModel @Inject constructor(private val suratKeluarRepo: SuratKeluarRepository): ViewModel() {
    
    private var _result = MutableLiveData<ResultDataResponse<SuratKeluar>?>()
    val result: LiveData<ResultDataResponse<SuratKeluar>?> get() = _result
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private var _reviewResult = MutableLiveData<ResultResponse>()
    val reviewResult: LiveData<ResultResponse> get() = _reviewResult
    
    fun getSuratKeluar(idSurat: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = suratKeluarRepo.getSuratKeluarByIdSurat(idSurat)
                _result.value = result
                _isLoading.value = false
            }catch (e: IOException){}
        }
    }
    
    fun reviewKonsepSurat(idSurat: Int, instruksiPenandatangan: String, statusSurat: String){
        viewModelScope.launch {
            try {
                _reviewResult.value = suratKeluarRepo.reviewKonsepSurat(idSurat, instruksiPenandatangan, statusSurat)
            }catch (e: IOException){}
        }
    }
    
}