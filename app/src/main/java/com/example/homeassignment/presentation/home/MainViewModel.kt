package com.example.homeassignment.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeassignment.domain.location.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationTracker: LocationTracker
): ViewModel() {

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState

    fun getCurrentLocation() = viewModelScope.launch {

        _locationState.value = locationState.value.copy(
            isLoading = true
        )

        locationTracker.getCurrentLocation()?.let {
            _locationState.value = locationState.value.copy(
                isLoading = false,
                location = it
            )
        }?: kotlin.run {
            _locationState.value = locationState.value.copy(
                isLoading = false,
                message = "Cannot get location. Make sure to grant permission and turn on the gps"
            )
        }
    }

}