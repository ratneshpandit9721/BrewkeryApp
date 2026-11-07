package com.brewkery.app.di

import com.brewkery.app.data.repository.BrewkeryRepositoryImpl
import com.brewkery.app.data.repository.CartRepositoryImpl
import com.brewkery.app.domain.repository.BrewkeryRepository
import com.brewkery.app.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBrewkeryRepository(
        impl: BrewkeryRepositoryImpl,
    ): BrewkeryRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl,
    ): CartRepository
}
