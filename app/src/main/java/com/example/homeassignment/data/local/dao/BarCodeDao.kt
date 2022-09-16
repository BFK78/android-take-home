package com.example.homeassignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.homeassignment.domain.model.BarCode
import kotlinx.coroutines.flow.Flow

@Dao
interface BarCodeDao {

    @Insert
    suspend fun insertBarCode(barCode: BarCode)

    @Query("SELECT * FROM BARCODE")
    fun getAllBarCode(): Flow<List<BarCode>>

}