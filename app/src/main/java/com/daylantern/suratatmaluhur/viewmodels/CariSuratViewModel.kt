package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.Surat
import com.daylantern.suratatmaluhur.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CariSuratViewModel @Inject constructor(private val suratMasukRepo: SuratMasukRepository): ViewModel(){
    
    private var _resultSurat = MutableLiveData<ResultListDataResponse<Surat>?>()
    val resultSurat :LiveData<ResultListDataResponse<Surat>?> get() = _resultSurat
    
    var tvHasilCari = ""
    
    fun clear(){
        _resultSurat.value = null
    }
    
    fun cariSurat(keyword: String, idPencari: Int, jenisSurat: String){
        viewModelScope.launch {
            _resultSurat.value = suratMasukRepo.cariSurat(keyword, idPencari, jenisSurat)
        }
    }
    
}