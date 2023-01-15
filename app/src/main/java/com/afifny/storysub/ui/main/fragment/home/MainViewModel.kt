package com.afifny.storysub.ui.main.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.afifny.storysub.data.StoryRepository
import com.afifny.storysub.data.remote.response.ListStoryItem

class MainViewModel(val repository: StoryRepository) : ViewModel() {
    var story: LiveData<PagingData<ListStoryItem>> =
        repository.getStoryList().cachedIn(viewModelScope)
}
