package com.daylantern.suratatmaluhur.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.entities.Instansi
import com.daylantern.suratatmaluhur.entities.Kategori
import com.daylantern.suratatmaluhur.entities.Pegawai
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
class TambahSuratKeluarViewModel @Inject constructor(
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
    
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _createMessage = MutableLiveData<ResultResponse>()
    val createMessage: LiveData<ResultResponse> get() = _createMessage
    
    private val _operationMessage = MutableLiveData<ResultResponse?>()
    val operationMessage: LiveData<ResultResponse?> get() = _operationMessage
    
    private var _listPegawai = MutableLiveData<List<Pegawai>>()
    val listPegawai: LiveData<List<Pegawai>> get() = _listPegawai
    
    private var _result = MutableLiveData<ResultDataResponse<Int>?>()
    val result: LiveData<ResultDataResponse<Int>?> get() = _result
    
    private var _kodeBagianPegawai = MutableLiveData<String?>()
    val kodeBagianPegawai : LiveData<String?> = _kodeBagianPegawai

    fun createKonsepSurat(
        perihal: String,
        isiSurat: String,
        idPembuat: Int,
        penandatangan: String,
        instansi: String,
        namaPenerima: String,
        jabatanPenerima: String,
        kategori: String,
        tembusan: String,
        fileLampiran: List<FileSuratModel>,
    ) {
        val idInstansi = _dataInstansi.value?.find { it.namaInstansi == instansi }?.idInstansi ?: return
        val idPenandatangan = _listPegawai.value?.find { it.nama == penandatangan }?.idPegawai ?: return
        val kodeKategori = _dataKategori.value?.find { it.kodeKategori == kategori }?.kodeKategori ?: return
        val files = mutableListOf<MultipartBody.Part>()
        fileLampiran.forEach { filePath ->
            val requestFile = filePath.file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file[]", filePath.file.path, requestFile)
            files.add(part)
        }
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _result.value = suratKeluarRepo.createKonsepSurat(
                    perihal,
                    isiSurat,
                    idPembuat.toString(),
                    idPenandatangan.toString(),
                    idInstansi.toString(),
                    namaPenerima,
                    jabatanPenerima,
                    kodeKategori,
                    tembusan,
                    files
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
    
    fun getPegawai() {
        viewModelScope.launch {
            try {
                _listPegawai.value = pegawaiRepo.getPegawai().data
            } catch (e: IOException) {
            
            }
        }
    }
    
    fun getPegawaiById(idPegawai: Int){
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.getLoggedInPegawai(idPegawai)
                _kodeBagianPegawai.value = result.data.kodeBagian
            }catch (e: IOException){
            }
        }
    }

}