package com.berco.spaceflightnews.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.time.Clock

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Injected rather than read statically so time can be fixed in tests. */
    @Provides
    fun provideClock(): Clock = Clock.System
}
