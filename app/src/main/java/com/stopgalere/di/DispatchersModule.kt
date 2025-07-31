package com.stopgalere.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher =
        Dispatchers.Main.immediate

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher =
        Dispatchers.IO
}
