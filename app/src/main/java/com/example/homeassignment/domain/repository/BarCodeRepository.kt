package com.example.homeassignment.domain.repository

import com.example.homeassignment.domain.model.BarCode
import kotlinx.coroutines.flow.Flow

interface BarCodeRepository {

    suspend fun insertBarCode(barCode: BarCode)

    suspend fun getAllBarCode(): Flow<List<BarCode>>

}