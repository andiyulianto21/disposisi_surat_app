package com.daylantern.suratatmaluhur.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SuratKeluar(
    @SerializedName("id_surat") val idSurat: Int,
    @SerializedName("no_surat") val noSurat: String?,
    @SerializedName("tgl_surat") val tglSurat: String,
    @SerializedName("tgl_review") val tglReview: String?,
    @SerializedName("nama_penginput") val namaPenginput: String?,
    @SerializedName("nama_penandatangan") val namaPenandatangan: String?,
    @SerializedName("instansi_penerima") val instansiPenerima: String,
    @SerializedName("nama_penerima") val namaPenerima: String?,
    @SerializedName("jabatan_penerima") val jabatanPenerima: String?,
    @SerializedName("perihal") val perihal: String,
    @SerializedName("status_surat") val statusSurat: String,
    @SerializedName("kategori_surat") val kategoriSurat: String,
    @SerializedName("instruksi_penandatangan") val instruksiPenandatangan: String?,
    @SerializedName("isi_surat") val isiSurat: String?,
    @SerializedName("tembusan") val tembusan: List<String>,
    @SerializedName("file_surat") val fileSurat: @RawValue ArrayList<FileSurat> = arrayListOf()
): Parcelable