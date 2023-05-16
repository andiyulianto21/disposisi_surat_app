package com.daylantern.arsipsuratpembinaan

import com.daylantern.arsipsuratpembinaan.entities.*
import com.daylantern.arsipsuratpembinaan.entities.Instansi
import com.daylantern.arsipsuratpembinaan.models.PegawaiModel
import com.daylantern.arsipsuratpembinaan.models.SifatModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAR1iRjkI:APA91bGXEvutraZLAayUPwo0RMj8-PcBnXXEYUbV9RiSZa_U-lexOH-ZERgdOldsyzFygRuGyxSXr3suRIND9maXztpMMuuV6X-z_vcbgRNnJTQiEcqMJu3mVplF9sBVxcvPPLSMsAs5"
    )
    @POST("https://fcm.googleapis.com/fcm/send")
    fun sendNotification(
        @Body request: FCMNotification
    )
    
    @FormUrlEncoded
    @POST("android/login")
    suspend fun login(
        @Field("nuptk") nuptk: String,
        @Field("password") password: String,
        @Field("token") token: String
    ): ResultDataResponse<Pegawai>
    
    @GET("android/hapus_token/{id}")
    suspend fun removeTokenNotif(@Path("id") idPegawai: Int): ResultResponse
    
    @GET("android/pegawai/{id}")
    fun getDataPegawai(@Path("id") idPegawai: Int): ResultDataResponse<PegawaiModel>
    
    @GET("android/pegawai")
    suspend fun getPegawai(): List<Pegawai>
    
    @GET("android/pegawai/{id}")
    suspend fun getLoggedInPegawai(
        @Path("id") idPegawai: Int
    ): ResultDataResponse<Pegawai>
    
    @FormUrlEncoded
    @POST("android/ubah_data/{id}")
    fun ubahData(
        @Path("id") idPegawai: Int,
        @Field("namaPegawai") nama: String,
        @Field("jabatanPegawai") jabatan: String,
    ): ResultDataResponse<PegawaiModel>
    
    @FormUrlEncoded
    @POST("android/verifikasi_password/{id}")
    suspend fun verifikasiPassword(
        @Path("id") idPegawai: Int,
        @Field("password_lama") passwordLama: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("android/ubah_password/{id}")
    suspend fun ubahPassword(
        @Path("id") idPegawai: Int,
        @Field("password_baru") passwordBaru: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("android/tambah_instansi")
    suspend fun addInstansi(
        @Field("nama") namaInstansi: String,
        @Field("alamat") alamatInstansi: String = ""
    ): ResultDataResponse<Instansi>
    
    @FormUrlEncoded
    @POST("android/hapus_instansi")
    suspend fun deleteInstansi(
        @Field("id_instansi") idInstansi: Int,
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("android/ubah_instansi")
    suspend fun updateInstansi(
        @Field("id_instansi") idInstansi: Int,
        @Field("nama_instansi") namaInstansi: String,
        @Field("alamat_instansi") alamatInstansi: String,
    ): ResultResponse
    
    @GET("android/tampilkan_instansi/{id}")
    suspend fun getInstansiById(
        @Path("id") idInstansi: Int? = null
    ): ResultDataResponse<List<Instansi>>
    
    @GET("android/tampilkan_instansi")
    suspend fun getInstansi(): ResultDataResponse<List<Instansi>>
    
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
    
    @GET("android/disposisi/")
    suspend fun getDisposisi(): ResultListDataResponse<SuratMasuk>
    
    @FormUrlEncoded
    @POST("android/laporan_surat_masuk")
    suspend fun getLaporanSuratMasuk(
        @Field("dari_tgl") dariTgl: String,
        @Field("sampai_tgl") sampaiTgl: String,
    ): ResultListDataResponse<SuratMasuk>

//    @FormUrlEncoded
//    @POST("android/tambah_surat_masuk")
//    suspend fun addSuratMasuk(
//        @Field("no_surat") noSurat: String,
//        @Field("id_instansi") idInstansi: String,
//        @Field("id_sifat") idSifat: String,
//        @Field("perihal") perihal: String,
//        @Field("tgl_surat_masuk") tglSuratMasuk: String,
//    ): ResultResponse
    
    @Multipart
    @POST("android/tambah_surat_masuk")
    suspend fun addSuratMasuk(
        @Part files: List<MultipartBody.Part>,
        @Part("no_surat") noSurat: RequestBody,
        @Part("id_instansi") idInstansi: RequestBody,
        @Part("id_sifat") idSifat: RequestBody,
        @Part("perihal") perihal: RequestBody,
        @Part("tgl_surat_masuk") tglSuratMasuk: RequestBody,
    ): ResultResponse
    
    @Multipart
    @POST("android/upload_file")
    suspend fun upload(
        @Part file: List<MultipartBody.Part>
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