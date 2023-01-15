package com.afifny.storysub.ui.main.fragment.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.afifny.storysub.adapter.StoryAdapter
import com.afifny.storysub.data.DataDummy
import com.afifny.storysub.data.StoryRepository
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.data.local.room.StoryDatabase
import com.afifny.storysub.data.remote.response.ListStoryItem
import com.afifny.storysub.data.remote.retrofit.ApiService
import com.afifny.storysub.utils.CoroutinesTestRule
import com.afifny.storysub.utils.MainDispatcherRule
import com.afifny.storysub.utils.PagedTestDataSource
import com.afifny.storysub.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryRepository
    private val dummyStory = DataDummy.generateDummyStoryEntity()

    @Test
    fun `when getStory is Success`() = runTest {
        val data : PagingData<ListStoryItem> = PagedTestDataSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        `when`(repository.getStoryList()).thenReturn(expectedStory)
        val mainViewModel = MainViewModel(repository)

        val actualResponse : PagingData<ListStoryItem> = mainViewModel.story.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualResponse)
        advanceUntilIdle()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
    }

    @Test
    fun `when getStory list is empty`() = runTest {
        val storyData = MutableLiveData<PagingData<ListStoryItem>>()
        storyData.value = PagingData.from(listOf())

        `when`(repository.getStoryList()).thenReturn(storyData)

        val mainViewModel = MainViewModel(repository)
        val actualStory = mainViewModel.story.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
        )
        differ.submitData(actualStory)
        advanceUntilIdle()
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(differ.snapshot().isEmpty())
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}