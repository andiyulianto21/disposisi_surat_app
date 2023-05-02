package com.daylantern.arsipsuratpembinaan.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.arsipsuratpembinaan.models.FileSuratModel
import com.daylantern.arsipsuratpembinaan.models.InstansiModel
import com.daylantern.arsipsuratpembinaan.models.SifatModel
import com.daylantern.arsipsuratpembinaan.repositories.InstansiRepository
import com.daylantern.arsipsuratpembinaan.repositories.SifatSuratRepository
import com.daylantern.arsipsuratpembinaan.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TambahSuratMasukViewModel @Inject constructor(
    private val instansiRepo: InstansiRepository,
    private val sifatRepo: SifatSuratRepository,
    private val suratMasukRepo: SuratMasukRepository
) : ViewModel() {
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _dataInstansi = MutableLiveData<List<InstansiModel>>()
    val dataInstansi: LiveData<List<InstansiModel>> get() = _dataInstansi

    private val _dataSifat = MutableLiveData<List<SifatModel>>()
    val dataSifat: LiveData<List<SifatModel>> get() = _dataSifat

    private val _errorBottomSheet = MutableLiveData<String?>()
    val errorBottomSheet: LiveData<String?> get() = _errorBottomSheet
    
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isSuccess = MutableLiveData<Boolean?>()
    val isSuccess: LiveData<Boolean?> get() = _isSuccess

    private val _selectedSifat = MutableLiveData<String>()
    val selectedSifat: LiveData<String> get() = _selectedSifat

    private val _selectedInstansi = MutableLiveData<String>()
    val selectedInstansi: LiveData<String> get() = _selectedInstansi

    init {
        _isSuccess.value = false
    }

    fun insertSurat(
        noSurat: String,
        perihal: String,
        tglSuratMasuk: String,
        listFile: List<String>
    ) {
        val idInstansi = getIdInstansiSelected(_dataInstansi.value!!.toList())
        val idSifat = getIdSifatSelected(_dataSifat.value!!.toList())
        if(idInstansi.isNullOrEmpty()) {
            _message.value = "Instansi Pengirim belum dipilih / kosong"
            return
        }
        if(idSifat.isNullOrEmpty()) {
            _message.value = "Sifat Surat belum dipilih / kosong"
            return
        }
        val files = mutableListOf<MultipartBody.Part>()
        listFile.forEach {filePath ->
            val file = File(filePath)
            val requestFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            val part = MultipartBody.Part.createFormData("file[]", file.name, requestFile)
            files.add(part)
        }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _message.value = ""
                val result = suratMasukRepo.addSuratMasuk(noSurat,
                    idInstansiParam = idInstansi,
                    idSifatParam = idSifat,
                    perihalParam = perihal,
                    tglSuratMasukParam = tglSuratMasuk,
                    listFile = files
                )
                if(result.status == 200){
                    _message.value = result.message
                    _isSuccess.value = true
                }else {
                    _message.value = result.message
                    _isSuccess.value = false
                }
                _isLoading.value = false
            }catch (e: Exception) {
                Log.d("fail", "insertSurat failed: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    fun changeValueInstansi(selected: String) {
        _selectedInstansi.value = selected
    }

    fun changeValueSifat(selected: String) {
        _selectedSifat.value = selected
    }

    private fun getIdInstansiSelected(list: List<InstansiModel>?): String?{
        if (list != null) {
            for (item in list)
                if(item.namaInstansi == _selectedInstansi.value)
                    return item.idInstansi
        }
        return null
    }

    private fun getIdSifatSelected(list: List<SifatModel>?): String?{
        if (list != null) {
            for (item in list)
                if(item.sifatSurat == _selectedSifat.value)
                    return item.idSifat
        }
        return null
    }

    fun fetchInstansi() {
        viewModelScope.launch {
            try {
                _dataInstansi.postValue(instansiRepo.getInstansi())
            }catch (e: Exception) {

            }
        }
    }

    fun fetchSifat() {
        viewModelScope.launch {
            try {
                _dataSifat.postValue(sifatRepo.getSifat())
            }catch (e: Exception){

            }
        }
    }

    fun insertInstansi(nama: String, alamat: String) {
        if (nama.isEmpty()) {
            _errorBottomSheet.value = "Form data belum diisi!"
            return
        }
        viewModelScope.launch {
            val result = instansiRepo.addInstansi(nama, alamat)
            when (result.status) {
                200 -> {
                    val data = result.data
                    _dataInstansi.value = _dataInstansi.value?.plus(data)

                    _selectedInstansi.value = data.namaInstansi

                    _errorBottomSheet.value = null
                }
                400 -> {
                    _errorBottomSheet.value = result.messages
                }
            }
        }
    }

    fun insertSifat(sifat: String) {
        if (sifat.isEmpty()) {
            _errorBottomSheet.value = "Form data belum diisi!"
            return
        }
        viewModelScope.launch {
            val result = sifatRepo.addSifat(sifat)
            when (result.status) {
                200 -> {
                    val data = result.data
                    _dataSifat.value = _dataSifat.value?.plus(data)

                    _selectedSifat.value = result.data.sifatSurat

                    _errorBottomSheet.value = null
                }
                400 -> {
                    _errorBottomSheet.value = result.messages
                }
            }
        }
    }

}