package com.daylantern.arsipsuratpembinaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.entities.ResultResponse
import com.daylantern.arsipsuratpembinaan.repositories.PegawaiRepository
import com.daylantern.arsipsuratpembinaan.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pegawaiRepo: PegawaiRepository
) :
    ViewModel() {
    
    private var _pegawaiLoggedIn = MutableLiveData<Pegawai?>()
    val pegawaiLoggedIn: LiveData<Pegawai?> get() = _pegawaiLoggedIn
    
    private var _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> get() = _isVerified
    
    private var _ubahPasswordMessage = MutableLiveData<ResultResponse?>()
    val ubahPasswordMessage: LiveData<ResultResponse?> get() = _ubahPasswordMessage
    
    init {
        _isVerified.value = false
        _ubahPasswordMessage.value = null
    }
    
    fun resetPasswordMessage() {
        _ubahPasswordMessage.value = null
    }
    
    fun changeVerification(value: Boolean) {
        _isVerified.value = value
    }
    
    fun verifikasiPassword(id: Int, passwordLama: String) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.verifikasiPassword(id, passwordLama)
                _ubahPasswordMessage.value = result
                if (result.status == 200)
                    changeVerification(true)
                else
                    changeVerification(false)
            } catch (e: Exception) {
            
            }
        }
    }
    
    fun ubahPassword(id: Int, passwordBaru: String) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.ubahPassword(id, passwordBaru)
                _ubahPasswordMessage.value = result
            } catch (e: Exception) {
            
            }
        }
    }
    
    fun fetchPegawaiLoggedIn(id: Int) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.getLoggedInPegawai(id)
                if (result.status == 200) {
                    _pegawaiLoggedIn.value = result.data
                }
            } catch (e: Exception) {
            
            }
        }
    }
    
    fun removeTokenNotif(idPegawai: Int) {
        viewModelScope.launch {
            pegawaiRepo.removeTokenNotif(idPegawai)
        }
    }
}