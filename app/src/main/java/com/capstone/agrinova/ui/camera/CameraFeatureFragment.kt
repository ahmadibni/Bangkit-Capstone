package com.capstone.agrinova.ui.camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.agrinova.databinding.FragmentCameraFeatureBinding

class CameraFeatureFragment : Fragment() {

    private var _binding: FragmentCameraFeatureBinding? = null
    private val binding get() = _binding!!


    private var bitmap : Bitmap? = null

    private val cameraPermission = android.Manifest.permission.CAMERA
    private val storagePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val mediaPermission = android.Manifest.permission.ACCESS_MEDIA_LOCATION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cameraFeatureViewModel =
            ViewModelProvider(this).get(CameraFeatureViewModel::class.java)

        _binding = FragmentCameraFeatureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardCamera.setOnClickListener { useCamera() }
        binding.cardGallery.setOnClickListener { openGallery() }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            bitmap = imageBitmap
            Glide.with(binding.previewImageView).load(imageBitmap).into(binding.previewImageView)
        }
    }
    private val documentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val contentResolver = requireActivity().contentResolver
        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        bitmap = imageBitmap
        Glide.with(binding.previewImageView).load(imageBitmap).into(binding.previewImageView)
    }

    fun useCamera(){
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                cameraPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(cameraPermission),
                6
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)
        }
    }

    fun openGallery(){
        if (ContextCompat.checkSelfPermission(requireActivity(), storagePermission) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(requireActivity() ,mediaPermission)) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(storagePermission,mediaPermission),
                4
            )
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            documentLauncher.launch(intent.type)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}