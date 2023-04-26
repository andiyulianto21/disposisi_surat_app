package com.daylantern.arsipsuratpembinaan

import com.daylantern.arsipsuratpembinaan.entities.*
import com.daylantern.arsipsuratpembinaan.models.InstansiModel
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import com.daylantern.arsipsuratpembinaan.models.SifatModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("android/login")
    fun login(
        @Field("nuptk") nuptk: String,
        @Field("password") password: String
    ): ResultDataResponse<PegawaiModel>

    @GET("android/pegawai/{id}")
    fun getDataPegawai(@Path("id") idPegawai: Int): ResultDataResponse<PegawaiModel>

    @GET("android/pegawai")
    suspend fun getPegawai(): List<Pegawai>

    @FormUrlEncoded
    @POST("android/ubah_data/{id}")
    fun ubahData(
        @Path("id") idPegawai: Int,
        @Field("namaPegawai") nama: String,
        @Field("jabatanPegawai") jabatan: String,
    ): ResultDataResponse<PegawaiModel>

    @FormUrlEncoded
    @POST("android/ubah_password/{id}")
    fun ubahPassword(
        @Path("id") idPegawaiModel: Int,
        @Field("isPasswordValid") isValid: Int,
        @Field("password_lama") passwordLama: String,
        @Field("password_baru") passwordBaru: String?
    ): ResultDataResponse<PegawaiModel>

    @FormUrlEncoded
    @POST("android/tambah_instansi")
    suspend fun addInstansi(
        @Field("nama") namaInstansi: String,
        @Field("alamat") alamatInstansi: String = ""
    ): ResultDataResponse<InstansiModel>

    @GET("android/tampilkan_instansi")
    suspend fun getInstansi(): List<InstansiModel>

    @FormUrlEncoded
    @POST("android/tambah_sifat")
    suspend fun addSifat(
        @Field("sifat") namaInstansi: String
    ): ResultDataResponse<SifatModel>

    @GET("android/tampilkan_sifat")
    suspend fun getSifat(): List<SifatModel>

    @GET("android/surat_masuk/{id}")
    suspend fun getSuratMasukById(@Path("id") idSuratMasuk: Int): ResultDataResponse<SuratMasuk>

    @GET("android/surat_masuk")
    suspend fun getSuratMasuk(): ResultListDataResponse<SuratMasuk>

    @FormUrlEncoded
    @POST("android/tambah_surat_masuk")
    suspend fun addSuratMasuk(
        @Field("no_surat") noSurat: String,
        @Field("id_instansi") idInstansi: String,
        @Field("id_sifat") idSifat: String,
        @Field("perihal") perihal: String,
        @Field("tgl_surat_masuk") tglSuratMasuk: String,
    ): ResultResponse

    @FormUrlEncoded
    @POST("android/tambah_disposisi")
    suspend fun addDisposisi(
        @Field("id_surat_masuk") idSuratMasuk: String,
        @Field("id_disposisi") idDisposisi: String,
        @Field("catatan_disposisi") catatanDisposisi: String,
        @Field("pihak_tujuan") pihakTujuan: String,
    ): ResultResponse
}