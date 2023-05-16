package com.daylantern.arsipsuratpembinaan.entities

import com.google.gson.annotations.SerializedName

data class SuratMasuk(
    @SerializedName("id_surat_masuk") val idSuratMasuk: String,
    @SerializedName("no_surat_masuk") val noSuratMasuk: String,
    @SerializedName("tgl_surat_masuk") val tglSuratMasuk: String,
    @SerializedName("tgl_surat_diterima") val tglSuratDiterima: String,
    @SerializedName("id_disposisi") val idDisposisi: String? = null,
    @SerializedName("tgl_disposisi") val tglDisposisi: String? = null,
    @SerializedName("instansi_pengirim") val instansiPengirim: String,
    @SerializedName("perihal") val perihal: String,
    @SerializedName("lampiran") val lampiran: String,
    @SerializedName("sifat_surat") val sifatSurat: String,
    @SerializedName("status_surat") val statusSurat: String,
    @SerializedName("catatan_disposisi") val catatanDisposisi: String? = null,
    @SerializedName("disposisi_tujuan") val disposisiTujuan: ArrayList<String> = arrayListOf(),
    @SerializedName("file_surat") val fileSurat: ArrayList<String> = arrayListOf()
)