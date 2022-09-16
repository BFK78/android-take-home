package com.example.homeassignment.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.homeassignment.data.local.dao.BarCodeDao
import com.example.homeassignment.domain.model.BarCode

@Database(
    entities = [BarCode::class],
    version = 1
)
abstract class BarcodeDatabase: RoomDatabase() {
    abstract val barCodeDao: BarCodeDao
}