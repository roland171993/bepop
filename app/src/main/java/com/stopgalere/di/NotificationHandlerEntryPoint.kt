package com.stopgalere.di

import com.stopgalere.util.NotificationClickHandler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotificationHandlerEntryPoint {
    fun handler(): NotificationClickHandler
}
