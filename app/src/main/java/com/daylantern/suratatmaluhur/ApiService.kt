package com.daylantern.suratatmaluhur

import com.daylantern.suratatmaluhur.entities.*
import com.daylantern.suratatmaluhur.entities.Instansi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    
    @FormUrlEncoded
    @POST("cari")
    suspend fun cariSurat(
        @Field("keyword") keyword: String,
        @Field("id_pencari") idPencari: Int,
        @Field("jenis_surat") jenisSurat: String,
    ): ResultListDataResponse<Surat>
    
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("kode_bagian") kodeBagian: String,
        @Field("nama") nama: String,
        @Field("jabatan") jabatan: String,
        @Field("level_akses") levelAkses: String,
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("token") token: String
    ): ResultDataResponse<Pegawai>
    
    @FormUrlEncoded
    @POST("lupa_password")
    suspend fun lupaPassword(
        @Field("email") email: String
    ): ResultDataResponse<String>
    
    @FormUrlEncoded
    @POST("verif_token_password")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("password_baru") passwordBaru: String,
        @Field("token") token: String
    ): ResultResponse
    
    @GET("verif_token/{id}")
    suspend fun verifikasiToken(@Path("id") idPegawai: Int): ResultResponse
    
    @FormUrlEncoded
    @POST("hapus_token_fcm/{id}")
    suspend fun deleteTokenNotif(@Path("id") idPegawai: Int, @Field("token_fcm") tokenFcm: String): ResultResponse
    
    @GET("pegawai")
    suspend fun getPegawai(): ResultListDataResponse<Pegawai>
    
    @GET("pegawai/{id}")
    suspend fun getLoggedInPegawai(
        @Path("id") idPegawai: Int
    ): ResultDataResponse<Pegawai>
    
    @FormUrlEncoded
    @POST("ubah_password/{id}")
    suspend fun ubahPassword(
        @Path("id") idPegawai: Int,
        @Field("password_sekarang") password_sekarang: String,
        @Field("password_baru") passwordBaru: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("tambah_instansi")
    suspend fun addInstansi(
        @Field("nama") namaInstansi: String,
        @Field("alamat") alamatInstansi: String = ""
    ): ResultResponse
    
    @DELETE("hapus_instansi/{id}")
    suspend fun deleteInstansi(
        @Path("id") idInstansi: Int,
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("ubah_instansi/{id}")
    suspend fun updateInstansi(
        @Path("id") idInstansi: Int,
        @Field("nama") namaInstansi: String,
        @Field("alamat") alamatInstansi: String,
    ): ResultResponse
    
    @GET("instansi/{id}")
    suspend fun getInstansiById(
        @Path("id") idInstansi: Int? = null
    ): ResultDataResponse<List<Instansi>>
    
    @GET("instansi")
    suspend fun getInstansi(): ResultDataResponse<List<Instansi>>
    
    @GET("bagian")
    suspend fun getBagian(): ResultListDataResponse<Bagian>
    
    @DELETE("hapus_bagian/{kode}")
    suspend fun deleteBagian(
        @Path("kode") kodeBagian: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("tambah_bagian")
    suspend fun addBagian(
        @Field("kode_bagian") kodeBagian: String,
        @Field("deskripsi") deskripsi: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("ubah_bagian/{kode}")
    suspend fun updateBagian(
        @Path("kode") kodeBagianParam: String,
        @Field("kode_bagian") kodeBagian: String,
        @Field("deskripsi") deskripsi: String
    ): ResultResponse
    
    @GET("kategori")
    suspend fun getKategori(): ResultListDataResponse<Kategori>
    
    @DELETE("hapus_kategori/{kode}")
    suspend fun deleteKategori(
        @Path("kode") kodeKategori: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("tambah_kategori")
    suspend fun addKategori(
        @Field("kode_kategori") kodeKategori: String,
        @Field("deskripsi") deskripsi: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("ubah_kategori/{kode}")
    suspend fun updateKategori(
        @Path("kode") kodeKategoriParam: String,
        @Field("kode_kategori") kodeKategori: String,
        @Field("deskripsi") deskripsi: String
    ): ResultResponse
    
    @FormUrlEncoded
    @POST("ubah_pegawai/{id}")
    suspend fun updatePegawai(
        @Path("id") idPegawai: Int,
        @Field("nama") nama: String,
        @Field("kode_bagian") kodeBagian: String,
        @Field("email") email: String,
        @Field("level_akses") levelAkses: String,
        @Field("jabatan") jabatan: String,
        @Field("password") password: String?,
    ): ResultResponse
    
    @DELETE("hapus_pegawai/{id}")
    suspend fun deletePegawai(
        @Path("id") idPegawai: Int,
    ): ResultResponse
    
    @GET("masuk/{id}")
    suspend fun getSuratMasukById(@Path("id") idSuratMasuk: Int): ResultDataResponse<SuratMasuk>
    
    @GET("masuk_as_pegawai/{id}")
    suspend fun getSuratMasukByIdPegawai(@Path("id") idPegawai: Int): ResultListDataResponse<SuratMasuk>
    
    @GET("masuk")
    suspend fun getSuratMasuk(): ResultListDataResponse<SuratMasuk>
    
    @GET("disposisi")
    suspend fun getDisposisi(): ResultListDataResponse<SuratMasuk>
    
    @FormUrlEncoded
    @POST("android/laporan_surat_masuk")
    suspend fun getLaporanSuratMasuk(
        @Field("dari_tgl") dariTgl: String,
        @Field("sampai_tgl") sampaiTgl: String,
    ): ResultListDataResponse<SuratMasuk>

    @Multipart
    @POST("android/tambah_ttd")
    suspend fun addTandaTangan(
        @Part file: MultipartBody.Part
    ): ResultResponse
    
    @Multipart
    @POST("tambah_surat_masuk")
    suspend fun addSuratMasuk(
        @Part files: List<MultipartBody.Part>,
        @Part("no_surat") noSurat: RequestBody,
        @Part("id_instansi") idInstansi: RequestBody,
        @Part("id_penginput") idPenginput: RequestBody,
        @Part("kode_kategori") kodeKategori: RequestBody,
        @Part("perihal") perihal: RequestBody,
        @Part("tgl_surat") tglSuratMasuk: RequestBody,
    ): ResultDataResponse<Int>
    
    @FormUrlEncoded
    @POST("tambah_disposisi")
    suspend fun addDisposisi(
        @Field("id_surat") idSurat: String,
        @Field("catatan_disposisi") catatanDisposisi: String,
        @Field("penerima_disposisi") penerimaDisposisi: String,
        @Field("sifat_disposisi") sifatDisposisi: String,
        @Field("id_pimpinan") idPimpinan: Int,
    ): ResultResponse
    
    //SURAT KELUAR
    @Multipart
    @POST("buat_konsep")
    suspend fun createKonsepSurat(
        @Part("perihal") perihal: RequestBody,
        @Part("isi_surat") isiSurat: RequestBody,
        @Part("id_penginput") idPembuat: RequestBody,
        @Part("id_penandatangan") idPenandatangan: RequestBody,
        @Part("id_instansi_penerima") idInstansiTujuan: RequestBody,
        @Part("nama_penerima") namaTujuan: RequestBody,
        @Part("jabatan_penerima") jabatanTujuan: RequestBody,
        @Part("kode_kategori") kodeKategori: RequestBody,
        @Part("tembusan") tembusan: RequestBody,
        @Part files: List<MultipartBody.Part>,
    ): ResultDataResponse<Int>
    
    @Multipart
    @POST("edit_konsep/{id}")
    suspend fun editSurat(
        @Path("id") idSurat: Int,
        @Part("perihal") perihal: RequestBody,
        @Part("isi_surat") isiSurat: RequestBody,
        @Part("id_instansi_penerima") idInstansiTujuan: RequestBody,
        @Part("nama_penerima") namaTujuan: RequestBody,
        @Part("jabatan_penerima") jabatanTujuan: RequestBody,
        @Part("kode_kategori") kodeKategori: RequestBody,
        @Part("tembusan") tembusan: RequestBody,
//        @Part files: List<MultipartBody.Part>,
    ): ResultDataResponse<Int>
    
    @FormUrlEncoded
    @POST("review_konsep/{id}")
    suspend fun reviewKonsepSurat(
        @Path("id") idSurat: Int,
        @Field("instruksi_penandatangan") instruksiPenandatangan: String,
        @Field("status_surat") statusSurat: String
    ): ResultResponse
    
    @GET("keluar/{id}")
    suspend fun getSuratKeluarByIdSurat(
        @Path("id") idSurat: Int
    ): ResultDataResponse<SuratKeluar>
    
    @GET("keluar_as_pegawai/{id}")
    suspend fun getSuratKeluarByIdPegawai(
        @Path("id") idPegawai: Int
    ): ResultListDataResponse<SuratKeluar>
    
    //NOTIFIKASI
    @GET("notifikasi/{id}")
    suspend fun getNotifikasi(
        @Path("id") idPegawai: Int
    ): ResultListDataResponse<Notifikasi>
    
    @GET("notifikasi_unread/{id}")
    suspend fun notifikasiUnread(
        @Path("id") idPegawai: Int
    ): Boolean
    
    @GET("notifikasi_dibaca/{id}")
    suspend fun notifikasiDibaca(
        @Path("id") idNotifikasi: Int
    ): Boolean
    
    @GET("hapus_notifikasi/{id}")
    suspend fun deleteNotifikasi(
        @Path("id") idKotakPesan: Int
    ): ResultResponse
}