package com.example.homeassignment.presentation.camera

import android.app.Application
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeassignment.domain.model.BarCode
import com.example.homeassignment.domain.repository.BarCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val application: Application,
    private val barCodeRepository: BarCodeRepository
): ViewModel() {

    private val _state = MutableStateFlow(emptyList<BarCode>())
    val state: StateFlow<List<BarCode>> = _state

    init {
        getAllBarCode()
    }

    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null
    val cameraProvider: LiveData<ProcessCameraProvider>
         get() {
             if (cameraProviderLiveData == null) {
                 cameraProviderLiveData = MutableLiveData()
                 val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
                 cameraProviderFuture.addListener(
                     {
                         try {
                             cameraProviderLiveData!!.value = cameraProviderFuture.get()
                         } catch (e: Exception) {
                             e.printStackTrace()
                         }
                     },
                     ContextCompat.getMainExecutor(application)
                 )
             }
             return cameraProviderLiveData!!
         }

    fun insertBarCode(barCode: BarCode) = viewModelScope.launch {
        barCodeRepository.insertBarCode(barCode = barCode)
    }

    private fun getAllBarCode() = viewModelScope.launch {
        barCodeRepository.getAllBarCode().collectLatest {
            Log.i("basim", it.toString())
            _state.value = it
        }
    }

}