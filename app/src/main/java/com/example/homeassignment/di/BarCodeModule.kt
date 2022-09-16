package com.example.homeassignment.di

import com.example.homeassignment.data.repository.BarCodeRepositoryImplementation
import com.example.homeassignment.domain.repository.BarCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BarCodeModule {

    @Binds
    @Singleton
    abstract fun bindBarCodeRepository(barCodeRepositoryImplementation: BarCodeRepositoryImplementation): BarCodeRepository

}