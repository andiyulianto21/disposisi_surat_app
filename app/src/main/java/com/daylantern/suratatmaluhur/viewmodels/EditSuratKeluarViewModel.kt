package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Instansi
import com.daylantern.suratatmaluhur.entities.Kategori
import com.daylantern.suratatmaluhur.entities.ResultDataResponse
import com.daylantern.suratatmaluhur.entities.ResultResponse
import com.daylantern.suratatmaluhur.models.FileSuratModel
import com.daylantern.suratatmaluhur.repositories.InstansiRepository
import com.daylantern.suratatmaluhur.repositories.KategoriRepository
import com.daylantern.suratatmaluhur.repositories.PegawaiRepository
import com.daylantern.suratatmaluhur.repositories.SuratKeluarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class EditSuratKeluarViewModel @Inject constructor(
    private val instansiRepo: InstansiRepository,
    private val suratKeluarRepo: SuratKeluarRepository,
    private val pegawaiRepo: PegawaiRepository,
    private val kategoriRepo: KategoriRepository,
) : ViewModel() {
    
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _dataInstansi = MutableLiveData<List<Instansi>>()
    val dataInstansi: LiveData<List<Instansi>> get() = _dataInstansi
    
    private val _dataKategori = MutableLiveData<List<Kategori>>()
    val dataKategori: LiveData<List<Kategori>> get() = _dataKategori

    private val _instansiMessage = MutableLiveData<ResultResponse?>()
    val instansiMessage: LiveData<ResultResponse?> get() = _instansiMessage
    
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _createMessage = MutableLiveData<ResultResponse>()
    val createMessage: LiveData<ResultResponse> get() = _createMessage
    
    private val _result = MutableLiveData<ResultDataResponse<Int>?>()
    val result: LiveData<ResultDataResponse<Int>?> get() = _result

    fun editSurat(
        idSurat: Int,
        perihal: String,
        isiSurat: String,
        instansi: String,
        namaPenerima: String,
        jabatanPenerima: String,
        kategori: String,
        tembusan: String,
    ) {
        val idInstansi = _dataInstansi.value?.find { it.namaInstansi == instansi }?.idInstansi ?: return
        val kodeKategori = _dataKategori.value?.find { it.kodeKategori == kategori }?.kodeKategori ?: return
//        val files = mutableListOf<MultipartBody.Part>()
//        fileLampiran.forEach { filePath ->
//            val requestFile = filePath.file.asRequestBody("image/jpg".toMediaTypeOrNull())
//            val part = MultipartBody.Part.createFormData("file[]", filePath.file.path, requestFile)
//            files.add(part)
//        }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _result.value = suratKeluarRepo.editSurat(
                    idSurat,
                    perihal,
                    isiSurat,
                    idInstansi.toString(),
                    namaPenerima,
                    jabatanPenerima,
                    kodeKategori,
                    tembusan
                )
            }catch (e: IOException) {
                _createMessage.value = ResultResponse(500, Constants.ERROR_NO_INTERNET)
            }finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clear(){
        _result.value = null
        _message.value = null
    }

    fun getInstansi() {
        viewModelScope.launch {
            try {
                val result = instansiRepo.getInstansi()
                _dataInstansi.value = result.data.toList()
            }catch (e: Exception) {

            }
        }
    }
    
    fun getKategori() {
        viewModelScope.launch {
            try {
                _dataKategori.value = kategoriRepo.getKategori().data
            }catch (e: IOException){}
        }
    }
    
    fun addInstansi(nama: String, alamat: String) {
        viewModelScope.launch {
            val result = instansiRepo.addInstansi(nama, alamat)
            _instansiMessage.value = ResultResponse(result.status, result.message)
//            if(result.status == 200){
//                val data = result.data
//                _dataInstansi.value = _dataInstansi.value?.plus(data)
//                _selectedInstansi.value = data.namaInstansi
//            }
        }
    }

}