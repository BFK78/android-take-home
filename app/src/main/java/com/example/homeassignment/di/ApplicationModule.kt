package com.example.homeassignment.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.homeassignment.data.local.dao.BarCodeDao
import com.example.homeassignment.data.local.database.BarcodeDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): BarcodeDatabase {
        return Room.databaseBuilder(
            application,
            BarcodeDatabase::class.java,
            "Barcode Database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBarcodeDao(
        database: BarcodeDatabase
    ): BarCodeDao {
        return database.barCodeDao
    }

}