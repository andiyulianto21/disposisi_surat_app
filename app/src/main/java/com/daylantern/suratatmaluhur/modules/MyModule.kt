package com.daylantern.suratatmaluhur.modules

import android.content.Context
import android.content.SharedPreferences
import com.daylantern.suratatmaluhur.ApiService
import com.daylantern.suratatmaluhur.Constants
import com.daylantern.suratatmaluhur.repositories.InstansiRepository
import com.daylantern.suratatmaluhur.repositories.SuratMasukRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyModule {

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)
        return client
    }

    @Singleton
    @Provides
    fun providesRetrofit(httpClient: OkHttpClient.Builder): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    inline fun <reified T> provideRepository(apiService: ApiService): T{
        return when(T::class){
          InstansiRepository::class -> InstansiRepository(apiService)
          SuratMasukRepository::class -> SuratMasukRepository(apiService)
        else -> throw IllegalArgumentException("Unknown Repository")
        }as T
    }

//
//    @Singleton
//    @Provides
//    fun providesSessionManager(sharedPreferences: SharedPreferences) {
//        return
//    }

}