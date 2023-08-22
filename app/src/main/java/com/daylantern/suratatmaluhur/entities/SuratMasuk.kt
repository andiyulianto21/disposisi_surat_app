package com.daylantern.suratatmaluhur.entities

import com.google.gson.annotations.SerializedName

data class SuratMasuk(
    @SerializedName("id_surat") val idSurat: String,
    @SerializedName("no_surat") val noSurat: String,
    @SerializedName("tgl_surat") val tglSurat: String,
    @SerializedName("tgl_input") val tglSuratDiinput: String?,
    @SerializedName("id_disposisi") val idDisposisi: String?,
    @SerializedName("tgl_disposisi") val tglDisposisi: String?,
    @SerializedName("nama_penginput") val namaPenginput: String,
    @SerializedName("nama_pendisposisi") val namaPendisposisi: String?,
    @SerializedName("instansi_pengirim") val instansiPengirim: String,
    @SerializedName("perihal") val perihal: String,
    @SerializedName("kategori_surat") val kategoriSurat: String,
    @SerializedName("status_surat") val statusSurat: String,
    @SerializedName("sifat_disposisi") val sifatDisposisi: String,
    @SerializedName("catatan_disposisi") val catatanDisposisi: String? = null,
    @SerializedName("penerima_disposisi") val penerimaDisposisi: ArrayList<String>? = arrayListOf(),
    @SerializedName("file_surat") val fileSurat: ArrayList<FileSurat>? = arrayListOf()
)