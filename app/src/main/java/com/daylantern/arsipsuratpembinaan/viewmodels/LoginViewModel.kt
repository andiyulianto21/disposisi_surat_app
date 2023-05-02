package com.daylantern.arsipsuratpembinaan.viewmodels

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.entities.ResultDataResponse
import com.daylantern.arsipsuratpembinaan.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository, private val sharedPref: SharedPreferences) :
    ViewModel() {

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun login(nuptk: String, password: String) {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.login(nuptk, password)
                if (result.status == 200) {
                    val editor = sharedPref.edit()
                    editor
                        .putBoolean("login", true)
                        .putString(Constants.PREF_JABATAN, result.data.jabatan)
                        .putInt(Constants.PREF_ID_PEGAWAI, result.data.idPegawai)
                        .apply()
                    _isSuccess.value = true
                    _errorMessage.value = ""
                } else {
                    _errorMessage.value = result.messages
                    _isSuccess.value = false
                }
            }catch (e: Exception){
                _errorMessage.value = e.message
            }
        }
    }
}