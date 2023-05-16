package com.daylantern.arsipsuratpembinaan.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.daylantern.arsipsuratpembinaan.Constants
import com.daylantern.arsipsuratpembinaan.Constants.KEY_PHOTO
import com.daylantern.arsipsuratpembinaan.R
import com.daylantern.arsipsuratpembinaan.databinding.FragmentCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var outputDirectory: File
    private var imageCapture: ImageCapture? = null
    private lateinit var navC: NavController



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        outputDirectory = getOutputDirectory()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navC = Navigation.findNavController(view)
        (activity as AppCompatActivity).supportActionBar?.title = "Ambil Foto"

        startCamera()
        binding.fabAmbilGambar.setOnClickListener {
            takePhoto()
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if(mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    @SuppressLint("SimpleDateFormat")
    private fun takePhoto(){
        val imageCapture = imageCapture ?: return

        val photoFile = File(outputDirectory, SimpleDateFormat(Constants.FILENAME_FORMAT).format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object :
            ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Berhasil mengambil foto!"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                navC.previousBackStackEntry?.savedStateHandle?.set(KEY_PHOTO, photoFile)
                navC.popBackStack()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.d("camera", "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.previewCamera.surfaceProvider)
            }
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner,cameraSelector,imageCapture,preview)
            } catch (e: Exception) {
                Log.d("camera", "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))


    }

}

