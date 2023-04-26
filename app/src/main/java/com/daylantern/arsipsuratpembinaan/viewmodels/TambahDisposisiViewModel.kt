package com.daylantern.arsipsuratpembinaan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daylantern.arsipsuratpembinaan.entities.Pegawai
import com.daylantern.arsipsuratpembinaan.entities.SuratMasuk
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import com.daylantern.arsipsuratpembinaan.models.PilihData
import com.daylantern.arsipsuratpembinaan.repositories.PegawaiRepository
import com.daylantern.arsipsuratpembinaan.repositories.SuratMasukRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TambahDisposisiViewModel @Inject constructor(
    private val suratMasukRepo: SuratMasukRepository,
    private val pegawaiRepo: PegawaiRepository
) : ViewModel() {

    private var _suratMasuk = MutableLiveData<SuratMasuk?>()
    val suratMasuk: LiveData<SuratMasuk?> get() = _suratMasuk

    private var _pihakTujuan = MutableLiveData<MutableList<String>>()
    val pihakTujuan: LiveData<MutableList<String>> get() = _pihakTujuan

    private var _listPegawai = MutableLiveData<List<PilihData>>()
    val listPegawai: LiveData<List<PilihData>?> get() = _listPegawai

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> get() = _isSuccess


    fun getIdPegawai(name: String): Int?{
        return _listPegawai.value?.find { it.title == name }?.id
    }


    fun saveDisposisi(
        idSuratMasuk: String,
        idDisposisi: String,
        catatanDisposisi: String,
        pihakTujuan: String,
    ) {
        viewModelScope.launch {
            try {
                val result = suratMasukRepo.addDisposisi(
                    idSuratMasuk,
                    idDisposisi,
                    catatanDisposisi,
                    pihakTujuan
                )
                if (result.status == 200) {
                    _message.value = result.message
                    _isSuccess.value = true
                }
                _isSuccess.value = false
            } catch (e: IOException) {
                _message.value = e.localizedMessage
                _isSuccess.value = false
            }
        }
    }

    fun fetchSuratMasuk(idSurat: Int) {
        viewModelScope.launch {
            try {
                val result = suratMasukRepo.getSuratMasukById(idSurat)
                if (result.status == 200) {
                    _suratMasuk.postValue(result.data)
                }
            } catch (e: IOException) {

            }
        }
    }

    fun fetchPegawai() {
        viewModelScope.launch {
            try {
                val result = pegawaiRepo.getPegawai()
                _listPegawai.value = result.map { PilihData(it.idPegawai, it.nama) }
            } catch (e: IOException) {

            }
        }
    }

    fun insertSelectedTujuan(tujuan: List<String>) {
        viewModelScope.launch {
            _pihakTujuan.value = tujuan.toMutableList()
            tujuan.forEach { value ->
                _listPegawai.value?.find { it.title == value }?.isChecked = true
            }
        }
    }

    fun removeSelectedTujuan(tujuan: String) {
        viewModelScope.launch {
            _pihakTujuan.value?.remove(tujuan)
            _listPegawai.value?.find { it.title == tujuan }?.isChecked = false
        }
    }

}