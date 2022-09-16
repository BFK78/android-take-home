package com.example.homeassignment.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BarCode(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val barCodeValue: String
)
