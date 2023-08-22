package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.entities.Instansi
import com.daylantern.suratatmaluhur.entities.Kategori
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.models.FileSuratModel
import com.daylantern.suratatmaluhur.repositories.InstansiRepository
import com.daylantern.suratatmaluhur.repositories.KategoriRepository
import com.daylantern.suratatmaluhur.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TambahSuratMasukViewModel @Inject constructor(
    private val instansiRepo: InstansiRepository,
    private val suratMasukRepo: SuratMasukRepository,
    private val kategoriRepo: KategoriRepository,
) : ViewModel() {
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _dataInstansi = MutableLiveData<List<Instansi>>()
    val dataInstansi: LiveData<List<Instansi>> get() = _dataInstansi
    
    private val _dataKategori = MutableLiveData<List<Kategori>>()
    val dataKategori: LiveData<List<Kategori>> get() = _dataKategori
    
    private val _operationMessage = MutableLiveData<ResultResponse?>()
    val operationMessage: LiveData<ResultResponse?> get() = _operationMessage
    
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message
    
    private val _addMessage = MutableLiveData<ResultDataResponse<Int>?>()
    val addMessage: LiveData<ResultDataResponse<Int>?> get() = _addMessage
    
    init {
        _addMessage.value = null
    }
    
    fun clear(){
        _addMessage.value = null
        _operationMessage.value = null
    }
    
    fun insertSurat(
        noSurat: String,
        perihal: String,
        tglSuratMasuk: String,
        listFile: List<FileSuratModel>,
        instansiPengirim: String,
        kategori: String,
        idPenginput: Int
    ) {
        val idInstansi =
            _dataInstansi.value?.find { it.namaInstansi == instansiPengirim }?.idInstansi ?: return
        val files = mutableListOf<MultipartBody.Part>()
        listFile.forEach { filePath ->
            val requestFile = filePath.file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file[]", filePath.file.path, requestFile)
            files.add(part)
        }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _message.value = ""
                val result = suratMasukRepo.addSuratMasuk(
                    noSurat,
                    idInstansi.toString(),
                    idPenginput.toString(),
                    kategori.substringBefore("-").trim(),
                    perihal,
                    tglSuratMasuk,
                    files
                )
                _addMessage.value = result
                _isLoading.value = false
            } catch (e: IOException) {
            
            }
        }
    }
    
    fun getKategori() {
        viewModelScope.launch {
            try {
                _dataKategori.value = kategoriRepo.getKategori().data
            } catch (e: IOException) {
            }
        }
    }
    
    fun getInstansi() {
        viewModelScope.launch {
            try {
                val result = instansiRepo.getInstansi()
                _dataInstansi.value = result.data ?: listOf()
            } catch (e: Exception) {
            
            }
        }
    }
    
    fun addInstansi(nama: String, alamat: String) {
        viewModelScope.launch {
            try {
                _operationMessage.value = instansiRepo.addInstansi(nama, alamat)
                getInstansi()
            } catch (e: IOException) {
            }
        }
    }
    
    fun addKategori(kodeKategori: String, deskripsi: String) {
        viewModelScope.launch {
            try {
                _operationMessage.value = kategoriRepo.addKategori(kodeKategori, deskripsi)
                getKategori()
            } catch (e: IOException) {
            }
        }
    }
    
}