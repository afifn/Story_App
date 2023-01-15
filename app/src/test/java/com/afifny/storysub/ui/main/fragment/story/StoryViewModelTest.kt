package com.afifny.storysub.ui.main.fragment.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afifny.storysub.data.DataDummy
import com.afifny.storysub.data.Result
import com.afifny.storysub.data.StoryRepository
import com.afifny.storysub.data.remote.response.StoryResponse
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
class StoryViewModelTest {

    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: StoryViewModel

    private val dummyStory = DataDummy.generateDummyStoryEntity()
    private val token = "token-1"
    private val fileImage = DataDummy.generateDummyMultipartFile()
    private val desc = DataDummy.generateDummyRequestBody()


    @Before
    fun setup() {
        viewModel = StoryViewModel(repository)
    }

    @Test
    fun `when upload story is successfully`() = runTest {
        val messageResponse = "success"
        val expectedResponse = StoryResponse(dummyStory, false, messageResponse)
        val expectedValue = MutableLiveData<Result<StoryResponse?>>()
        expectedValue.value = Result.Success(expectedResponse)

        `when`(
            viewModel.uploadStory(
                token,
                desc,
                fileImage
            )
        ).thenReturn(expectedValue)
        val actualStory = viewModel.uploadStory(token, desc, fileImage).getOrAwaitValue()
        verify(repository).uploadStory(token, desc, fileImage)

        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
        assertEquals(messageResponse, (actualStory as Result.Success).data?.message)
    }

    @Test
    fun `when upload story is failed`() = runTest {
        val messageResponse = "error"
        val expectedResponse = StoryResponse(dummyStory, true, messageResponse)
        val expectedValue = MutableLiveData<Result<StoryResponse?>>()
        expectedValue.value = Result.Error(expectedResponse.message)

        `when`(viewModel.uploadStory(token, desc, fileImage)).thenReturn(expectedValue)
        val actualStory = viewModel.uploadStory(token, desc, fileImage).getOrAwaitValue()
        verify(repository).uploadStory(token, desc, fileImage)

        assertTrue(actualStory is Result.Error)
        assertEquals(messageResponse, (actualStory as Result.Error).error)
    }
}