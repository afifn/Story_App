package com.afifny.storysub.ui.main.fragment.story

import androidx.lifecycle.ViewModel
import com.afifny.storysub.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = repository.uploadStory(token, description, photo, lat, lon)
}