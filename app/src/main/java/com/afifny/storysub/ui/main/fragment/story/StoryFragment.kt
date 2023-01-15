package com.afifny.storysub.ui.main.fragment.story

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.R
import com.afifny.storysub.data.Result
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.databinding.FragmentStoryBinding
import com.afifny.storysub.ui.main.MainActivity
import com.afifny.storysub.utils.createCustomTempFile
import com.afifny.storysub.utils.reduceFileImage
import com.afifny.storysub.utils.uriToFile
import com.afifny.storysub.viewModel.DataStoreViewModel
import com.afifny.storysub.viewModel.MainViewModelFactory
import com.afifny.storysub.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class StoryFragment : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var viewModel: StoryViewModel
    private lateinit var mainViewModel: DataStoreViewModel

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location: Location? = null

    private var getFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupAction()
    }

    private fun setupLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { locate ->
                if (locate != null) {
                    this.location = locate
                    Log.d("TAG", "setupLocation: ${location!!.latitude} ${location!!.latitude}")
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.location_message_activated),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            when {
                permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    setupLocation()
                }
                permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    setupLocation()
                }
                else -> {
                    binding.switchLocation.isChecked = false
                }
            }
        }

    private fun setupAction() {
        binding.buttonCamera.setOnClickListener { startIntentCamera() }
        binding.buttonGallery.setOnClickListener { startIntentGallery() }
        binding.buttonAdd.setOnClickListener { addStory() }
        binding.switchLocation.setOnCheckedChangeListener { _, b ->
            if (b) {
                setupLocation()
            } else {
                this.location = null
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(requireContext().dataStore))
        )[DataStoreViewModel::class.java]
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireContext())
        )[StoryViewModel::class.java]
    }

    private fun showLoading(b: Boolean?) {
        if (b == true) {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonAdd.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.buttonAdd.isEnabled = true
        }
    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcGallery.launch(chooser)
    }

    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createCustomTempFile(requireContext()).also {
            val photoUrl: Uri =
                FileProvider.getUriForFile(requireContext(), "com.afifny.storysub", it)
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUrl)
            launchCamera.launch(intent)
        }
    }

    private val launcGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectImg, requireContext())
            getFile = myFile
            binding.ivPreview.setImageURI(selectImg)
        }
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.ivPreview.setImageBitmap(result)
        }
    }

    private fun addStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edAddDescription.text.toString()

            val desc = description.toRequestBody("text/plan".toMediaType())
            val reqImage = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                reqImage
            )

            var lat: RequestBody? = null
            var lon: RequestBody? = null
            if (location != null) {
                lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }
            mainViewModel.getUserLogin().observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    viewModel.uploadStory(user.token, desc, imageMultiPart, lat, lon)
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    val message = result.data?.message

                                    Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                                        .show()
                                    startActivity(Intent(activity, MainActivity::class.java))
                                    activity?.finish()
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    val messageError = result.error
                                    Toast.makeText(activity, messageError, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                }
            }
        }
    }
}

