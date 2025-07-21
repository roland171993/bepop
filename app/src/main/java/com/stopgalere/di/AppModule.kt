package com.stopgalere.di

import android.content.Context
import com.stopgalere.data.local.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext ctx: Context): Prefs =
        Prefs(ctx)
}
