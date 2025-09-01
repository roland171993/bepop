package com.stopgalere.di

import android.content.Context
import androidx.room.Room
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.local.Prefs
import com.stopgalere.data.remote.ApiService
import com.stopgalere.data.repository.JobRepository
import com.stopgalere.domain.repository.JobRepoInterface
import com.stopgalere.util.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .writeTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS).build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room
            .databaseBuilder(ctx, AppDatabase::class.java, AppConstants.DATABASE_NAME)
            .build()

    @Provides @Singleton
    fun provideJobRepo(db: AppDatabase, api: ApiService): JobRepoInterface =
        JobRepository(db, api)

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext ctx: Context): Prefs =
        Prefs(ctx)

    @Provides @Singleton
    fun provideNetworkMonitor(@ApplicationContext ctx: Context): com.stopgalere.data.remote.NetworkMonitor =
        com.stopgalere.data.remote.NetworkMonitor(ctx)

}
