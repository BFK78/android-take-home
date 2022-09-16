package com.example.homeassignment.presentation.camera

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassignment.R
import com.example.homeassignment.databinding.ActivityCameraBinding
import com.example.homeassignment.databinding.BottomSheetBinding
import com.example.homeassignment.databinding.RecyclerItemBinding
import com.example.homeassignment.domain.adapter.BarcodeAdapter
import com.example.homeassignment.domain.model.BarCode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    private lateinit var adapter: BarcodeAdapter
    private lateinit var barCodeList: List<BarCode>
    private lateinit var binding: ActivityCameraBinding
    private var previewView: PreviewView? = null
    private var cameraSelector: CameraSelector? = null
    private var previewUseCase: Preview? = null
    private val cameraViewModel: CameraViewModel by viewModels()
    private var cameraProvider: ProcessCameraProvider? = null
    private var analysisUseCase: ImageAnalysis? = null

    private val screenAspectRatio: Int
        get() {
            val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            cameraViewModel.state.collectLatest {
                barCodeList = it
                if (this@CameraActivity::adapter.isInitialized) {
                    adapter.setUpList(it)
                }
            }
        }

        setUpCamera()
    }

    private fun setUpCamera() {

        previewView = binding.previewView
        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        cameraViewModel.cameraProvider.observe(this, Observer {
            cameraProvider = it

            if (isCameraPermissionGranted()) {
                bindCameraUseCase()
            }
        })
    }

    private fun bindCameraUseCase() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {

        if (cameraProvider == null) {
            return
        }

        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(previewView!!.display.rotation)
            .build()

        previewUseCase!!.setSurfaceProvider(previewView!!.createSurfaceProvider())

        try {

            cameraProvider!!.bindToLifecycle(this, cameraSelector!!, previewUseCase)

        } catch (e: Exception) {
            Log.i("basim", e.toString())
        }

    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun bindAnalyseUseCase() {

        val barcodeScanner = BarcodeScanning.getClient()
        if (cameraProvider == null) {
            return
        }

        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(previewView!!.display.rotation)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(
            cameraExecutor,
            ImageAnalysis.Analyzer {
                processImageProxy(barcodeScanner, it)
            }
        )

        try {
            cameraProvider!!.bindToLifecycle(this, cameraSelector!!, analysisUseCase)
        } catch (e: Exception) {
            Log.i("error", e.message.toString())
        }
    }

    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(inputImage).addOnSuccessListener { barcodes ->
            barcodes.forEach {
                Log.i("barcode", "${it.displayValue.toString()}  ${it.rawValue.toString()}  "  )
                if (it.rawValue.toString().length >= 10) {
                    cameraProvider?.unbind(analysisUseCase)
                    cameraViewModel.insertBarCode(BarCode(barCodeValue = it.rawValue.toString()))
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.playAnimation()

                    binding.animationView.addAnimatorListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                           setUpBottomSheet(it.rawValue.toString())
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationRepeat(p0: Animator?) {
                            TODO("Not yet implemented")
                        }

                    })
                    Toast.makeText(this, "Barcode successfully saved", Toast.LENGTH_SHORT).show()

                } else {
                    cameraProvider?.unbind(analysisUseCase)
                    Toast.makeText(this, "values is less than 10", Toast.LENGTH_SHORT).show()
                    binding.error.visibility = View.VISIBLE
                    binding.error.playAnimation()

                    binding.error.addAnimatorListener(object: Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            finish()
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                            TODO("Not yet implemented")
                        }

                        override fun onAnimationRepeat(p0: Animator?) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }
        }.addOnFailureListener {
            Log.i("barcode", it.message.toString())
        }.addOnCompleteListener {
         
            imageProxy.close()
        }
    }

    private fun setUpBottomSheet(barcode: String) {
        val bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(R.layout.bottom_sheet)

        val recyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.recycler_bar)

        adapter = BarcodeAdapter(this)

        adapter.setUpList(barCodeList)

        recyclerView!!.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

        bottomSheetDialog.setOnDismissListener {
            finish()
        }

        bottomSheetDialog.show()
    }


    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}