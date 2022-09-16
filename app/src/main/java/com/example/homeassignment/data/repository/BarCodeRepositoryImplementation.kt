package com.example.homeassignment.data.repository

import com.example.homeassignment.data.local.dao.BarCodeDao
import com.example.homeassignment.domain.model.BarCode
import com.example.homeassignment.domain.repository.BarCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BarCodeRepositoryImplementation @Inject constructor(
    private val dao: BarCodeDao
): BarCodeRepository {

    override suspend fun insertBarCode(barCode: BarCode) {
        dao.insertBarCode(barCode = barCode)
    }

    override suspend fun getAllBarCode(): Flow<List<BarCode>> {
        return dao.getAllBarCode()
    }

}