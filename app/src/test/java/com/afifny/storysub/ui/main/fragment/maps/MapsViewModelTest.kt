package com.afifny.storysub.ui.main.fragment.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afifny.storysub.data.DataDummy
import com.afifny.storysub.data.Result
import com.afifny.storysub.data.StoryRepository
import com.afifny.storysub.data.remote.response.StoryResponse
import com.afifny.storysub.utils.CoroutinesTestRule
import com.afifny.storysub.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: MapsViewModel

    private val dummyList = DataDummy.generateDummyStoryEntity()
    private val token = "token-1"

    @Before
    fun setup() {
        viewModel = MapsViewModel(repository)
    }

    @Test
    fun `get story data with location - return success`() = runTest {
        val messageResponses = "success"
        val expectedResponse = StoryResponse(dummyList, false, messageResponses)
        val expectedValue = MutableLiveData<Result<StoryResponse?>>()
        expectedValue.value = Result.Success(expectedResponse)

        `when`(viewModel.getStoryLocation(token)).thenReturn(expectedValue)
        val actualStory = viewModel.getStoryLocation(token).getOrAwaitValue()
        verify(repository).getStoryLocation(token)

        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
        assertEquals(messageResponses, (actualStory as Result.Success).data?.message)
    }

    @Test
    fun `get story data with location - result error`() = runTest {
        val messageResponse = "error"
        val expectedResponse = StoryResponse(dummyList, true, messageResponse)
        val expectedValue = MutableLiveData<Result<StoryResponse?>>()
        expectedValue.value = Result.Error(expectedResponse.message)

        `when`(viewModel.getStoryLocation(token)).thenReturn(expectedValue)
        val actualStory = viewModel.getStoryLocation(token).getOrAwaitValue()
        verify(repository).getStoryLocation(token)

        assertTrue(actualStory is Result.Error)
        assertEquals(messageResponse, (actualStory as Result.Error).error)
    }
}