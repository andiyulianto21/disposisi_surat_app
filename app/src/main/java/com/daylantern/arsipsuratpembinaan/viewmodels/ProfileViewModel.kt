package com.daylantern.arsipsuratpembinaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository) :
    ViewModel() {

    private var _pegawaiLoggedIn = MutableLiveData<Pegawai?>()
    val pegawaiLoggedIn: LiveData<Pegawai?> get() = _pegawaiLoggedIn

    fun fetchPegawaiLoggedIn(id: Int) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.getLoggedInPegawai(id)
                if(result.status == 200){
                    _pegawaiLoggedIn.value = result.data
                }
            }catch (e: Exception){

            }
        }
    }
}