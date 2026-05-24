package com.stopgalere.di

import android.content.Context
import androidx.room.Room
import com.stopgalere.data.local.AppDatabase
import com.stopgalere.data.local.Prefs
import com.stopgalere.data.remote.ApiService
import com.stopgalere.data.repository.AiRepository
import com.stopgalere.data.repository.AuthRepository
import com.stopgalere.data.repository.ChatRepository
import com.stopgalere.data.repository.CoverLetterRepository
import com.stopgalere.data.repository.JobRepository
import com.stopgalere.domain.repository.AiRepoInterface
import com.stopgalere.domain.repository.AuthRepoInterface
import com.stopgalere.domain.repository.ChatRepoInterface
import com.stopgalere.domain.repository.CoverLetterRepoInterface
import com.stopgalere.domain.repository.JobRepoInterface
import com.stopgalere.data.remote.webrtc.WebRtcManager
import com.stopgalere.util.AppConstants
import com.stopgalere.util.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ----------------------------------------------------------------
    // OkHttp — includes AuthInterceptor for automatic JWT injection
    // ----------------------------------------------------------------
    @Provides @Singleton
    fun provideOkHttp(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)                    // JWT Bearer token
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(AppConstants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()

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

    // ----------------------------------------------------------------
    // Room
    // ----------------------------------------------------------------
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, AppConstants.DATABASE_NAME)
            .fallbackToDestructiveMigration(true)
            .build()

    // ----------------------------------------------------------------
    // SharedPreferences
    // ----------------------------------------------------------------
    @Provides @Singleton
    fun providePrefs(@ApplicationContext ctx: Context): Prefs = Prefs(ctx)

    // ----------------------------------------------------------------
    // Repositories
    // ----------------------------------------------------------------
    @Provides @Singleton
    fun provideJobRepo(db: AppDatabase, api: ApiService): JobRepoInterface =
        JobRepository(db, api)

    @Provides @Singleton
    fun provideCoverLetterRepo(db: AppDatabase, api: ApiService): CoverLetterRepoInterface =
        CoverLetterRepository(db, api)

    @Provides @Singleton
    fun provideAuthRepo(api: ApiService, prefs: Prefs): AuthRepoInterface =
        AuthRepository(api, prefs)

    @Provides @Singleton
    fun provideChatRepo(api: ApiService): ChatRepoInterface =
        ChatRepository(api)

    @Provides @Singleton
    fun provideAiRepo(api: ApiService, @ApplicationContext ctx: Context): AiRepoInterface =
        AiRepository(api, ctx)

    // ----------------------------------------------------------------
    // WebRTC
    // ----------------------------------------------------------------
    @Provides @Singleton
    fun provideWebRtcManager(@ApplicationContext ctx: Context): WebRtcManager = WebRtcManager(ctx)

    // ----------------------------------------------------------------
    // Application-scoped coroutine scope
    // ----------------------------------------------------------------
    @Provides @Singleton @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
