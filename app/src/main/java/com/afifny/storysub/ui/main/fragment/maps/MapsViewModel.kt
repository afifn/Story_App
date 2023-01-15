package com.afifny.storysub.ui.main.fragment.maps

import androidx.lifecycle.ViewModel
import com.afifny.storysub.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStoryLocation(token: String) = repository.getStoryLocation(token)
}