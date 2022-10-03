package com.afifny.storysub.ui.main.fragment.story

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afifny.storysub.databinding.FragmentStoryBinding
import com.afifny.storysub.model.UserPref
import com.afifny.storysub.ui.main.MainActivity
import com.afifny.storysub.ui.main.MainViewModel
import com.afifny.storysub.utils.createCustomTempFile
import com.afifny.storysub.utils.reduceFileImage
import com.afifny.storysub.utils.uriToFile
import com.afifny.storysub.viewModel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
class StoryFragment : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var viewModel: StoryViewModel
    private lateinit var mainViewModel: MainViewModel

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

    private fun setupAction() {
        binding.buttonCamera.setOnClickListener { startIntentCamera() }
        binding.buttonGallery.setOnClickListener { startIntentGallery() }
        binding.buttonAdd.setOnClickListener { addStory() }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this, ViewModelFactory(UserPref.getInstance(requireContext().dataStore)))[MainViewModel::class.java]
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPref.getInstance(requireContext().dataStore)))[StoryViewModel::class.java]
        viewModel.isLad.observe(viewLifecycleOwner) {
            showLoding(it)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            error ->
            if (!error) {
                Toast.makeText(activity, "Upload success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun showLoding(b: Boolean?) {
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
            val photoUrl: Uri = FileProvider.getUriForFile(requireContext(),"com.afifny.storysub", it)
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
            mainViewModel.getUserLogin().observe(viewLifecycleOwner) { user->
                if (user != null) {
                    viewModel.uploadStory(user.token, desc, imageMultiPart)
                }
            }
        }
    }
}

