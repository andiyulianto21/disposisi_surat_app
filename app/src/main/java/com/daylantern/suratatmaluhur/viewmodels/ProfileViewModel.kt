package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Pegawai
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.NotifikasiRepository
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pegawaiRepo: PegawaiRepository,
    private val pesanRepo: NotifikasiRepository,
) :
    ViewModel() {
    
    private var _pegawaiLoggedIn = MutableLiveData<Pegawai?>()
    val pegawaiLoggedIn: LiveData<Pegawai?> get() = _pegawaiLoggedIn
    
    private var _isNotifikasiUnread = MutableLiveData<Boolean?>()
    val isNotifikasiUnread: LiveData<Boolean?> get() = _isNotifikasiUnread
    
    private var _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> get() = _isVerified
    
    private var _ubahPasswordMessage = MutableLiveData<ResultResponse?>()
    val ubahPasswordMessage: LiveData<ResultResponse?> get() = _ubahPasswordMessage
    
    private var _logoutMessage = MutableLiveData<ResultResponse?>()
    val logoutMessage: LiveData<ResultResponse?> get() = _logoutMessage
    
    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private var _errorNetworkMessage = MutableLiveData<String?>()
    val errorNetworkMessage: LiveData<String?> = _errorNetworkMessage
    
    init {
        _isVerified.value = false
        _ubahPasswordMessage.value = null
        _logoutMessage.value = null
        _isNotifikasiUnread.value = null
    }
    
    fun resetPasswordMessage() {
        _ubahPasswordMessage.value = null
    }
    
    fun changeVerification(value: Boolean) {
        _isVerified.value = value
    }
    
    fun notifikasiUnread(idPegawai: Int) {
        viewModelScope.launch {
            _isNotifikasiUnread.value = pesanRepo.notifikasiUnread(idPegawai)
        }
    }
    
//    fun verifikasiPassword(id: Int, passwordLama: String) {
//        viewModelScope.launch {
//            try {
//                val result = pegawaiRepo.verifikasiPassword(id, passwordLama)
//                _ubahPasswordMessage.value = result
//                if (result.status == 200)
//                    changeVerification(true)
//                else
//                    changeVerification(false)
//                _errorMessage.value = null
//            } catch (e: IOException) {
//                _errorMessage.value = Constants.ERROR_NO_INTERNET
//            }
//        }
//    }
    
    fun ubahPassword(id: Int, passwordSekarang: String, passwordBaru: String) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.ubahPassword(id, passwordSekarang, passwordBaru)
                _ubahPasswordMessage.value = result
            } catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
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
            } catch (e: IOException) {
                _errorNetworkMessage.value = Constants.ERROR_NO_INTERNET
            }
        }
    }
    
    fun deleteTokenNotif(idPegawai: Int, tokenFcm: String) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.deleteTokenNotif(idPegawai, tokenFcm)
                _logoutMessage.value = result
                _errorMessage.value = null
            }catch (e: IOException) {
                _errorMessage.value = Constants.ERROR_NO_INTERNET
            }
        }
    }
}