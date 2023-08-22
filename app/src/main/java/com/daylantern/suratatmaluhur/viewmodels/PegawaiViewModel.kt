package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.Bagian
import com.daylantern.suratatmaluhur.entities.Pegawai
import com.daylantern.suratatmaluhur.entities.ResultListDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.repositories.BagianRepository
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PegawaiViewModel @Inject constructor(private val pegawaiRepo: PegawaiRepository, private val bagianRepo: BagianRepository): ViewModel(){
    
    private var _pegawai = MutableLiveData<ResultListDataResponse<Pegawai>>()
    val pegawai: LiveData<ResultListDataResponse<Pegawai>> get() = _pegawai
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private var _bagian = MutableLiveData<ResultListDataResponse<Bagian>>()
    val bagian: LiveData<ResultListDataResponse<Bagian>> get() = _bagian
    
    private var _operationMessage = MutableLiveData<ResultResponse>()
    val operationMessage: LiveData<ResultResponse> get() = _operationMessage
    
    var isToastShown = false
    
    fun getPegawai(){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _pegawai.value = pegawaiRepo.getPegawai()
                _isLoading.value = false
            }catch (e: IOException){}
            
        }
    }
    
    fun getBagian(){
        viewModelScope.launch {
            _bagian.value = bagianRepo.getBagian()
        }
    }
    
    fun tambahPegawai(email: String,password: String,kodeBagian: String,nama: String, jabatan: String, levelAkses: String){
        viewModelScope.launch {
            try {
                isToastShown = false
                _operationMessage.value = pegawaiRepo.register(email, password, kodeBagian, nama, jabatan, levelAkses)
                if(_operationMessage.value?.status == 200) getPegawai()
            }catch (e: IOException){}
        }
    }
    
    fun deletePegawai(idPegawai: Int) {
        viewModelScope.launch {
            try {
                isToastShown = false
                _operationMessage.value = pegawaiRepo.deletePegawai(idPegawai)
                if(_operationMessage.value?.status == 200) getPegawai()
            }catch (e: IOException){}
        }
    }
    
    fun updatePegawai(
        idPegawai: Int,
        nama: String,
        kodeBagian: String,
        email: String,
        levelAkses: String,
        jabatan: String,
        password: String? =null,
    ){
        viewModelScope.launch {
            try {
                isToastShown = false
                _operationMessage.value = pegawaiRepo.updatePegawai(idPegawai, nama, kodeBagian, email, levelAkses, jabatan, password)
                if(_operationMessage.value?.status == 200) getPegawai()
            }catch (e:IOException){}
        }
    }
    
    fun addTandaTangan(file: MultipartBody.Part) {
        viewModelScope.launch {
            val result = pegawaiRepo.addTandaTangan(file)
            
        }
    }
    
    fun resetTokenNotifikasi(idPegawai: Int){
        viewModelScope.launch {
            try {
//                _operationMessage.value = pegawaiRepo.deleteTokenNotif(idPegawai)
                if (_operationMessage.value?.status == 200) getPegawai()
            }catch (e: IOException){}
        }
    }
    
}