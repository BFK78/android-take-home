package com.example.homeassignment.presentation.home

import android.location.Location

data class LocationState(
    val isLoading: Boolean = false,
    val location: Location? = null,
    val message: String = ""
)
