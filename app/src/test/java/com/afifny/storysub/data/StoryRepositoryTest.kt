package com.afifny.storysub.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.data.local.room.StoryDatabase
import com.afifny.storysub.data.remote.retrofit.ApiService
import com.afifny.storysub.utils.CoroutinesTestRule
import com.afifny.storysub.utils.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class StoryRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var pref: UserPref
    private lateinit var storyDatabase: StoryDatabase
    private lateinit var storyRepository: StoryRepository
    private lateinit var apiService: ApiService
    private val dataDummy = DataDummy.generateDummyStoryEntity()
    private val token = "token"
    private val token_false = "token_false"
    private val fileImage = DataDummy.generateDummyMultipartFile()
    private val desc = DataDummy.generateDummyRequestBody()

    @Before
    fun setup() {
        apiService = FakeApiService()
        storyDatabase = FakeStoryDatabase()
        storyRepository = StoryRepository(storyDatabase, apiService, pref)
    }

    @Test
    fun `when get story should not null`() = runTest {
        val actualValue = storyRepository.getStoryList()

        actualValue.observeForTesting {
            assertNotNull(actualValue)
        }
    }

    @Test
    fun `when get story location return story has latitude and longitude`() = runTest {
        val expectedValue = dataDummy
        val actualValue = storyRepository.getStoryLocation(token)
        actualValue.observeForTesting {
            assertNotNull(actualValue)
            assertTrue((actualValue.value is Result.Success))
            assertEquals(expectedValue.size, (actualValue.value as Result.Success).data?.listStory?.size)
            assertTrue((actualValue.value as Result.Success).data?.listStory?.get(0)?.lat != null)
            assertTrue((actualValue.value as Result.Success).data?.listStory?.get(0)?.lon != null)
        }
    }

    @Test
    fun `when get story location with empty data`() = runTest {
        val expectedResponse = DataDummy.generateDummyStoryEmptyResponse()
        val actualValue = storyRepository.getStoryLocation(token_false)
        actualValue.observeForTesting {
            assertNotNull(actualValue)
            assertTrue(expectedResponse.body()?.listStory?.isEmpty() == true)
            assertEquals(expectedResponse.body()?.listStory?.size, (actualValue.value as Result.Success).data?.listStory?.size)
        }
    }

    @Test
    fun `when upload story is successfully `() = runTest {
        val expectedResponse = DataDummy.generateDummyStoryResponse()
        val actualValue2 = storyRepository.uploadStory(token, desc, fileImage, null, null)
        actualValue2.observeForTesting {
            assertNotNull(actualValue2)
            assertEquals((actualValue2.value as Result.Success).data?.message, expectedResponse.body()?.message)
        }
    }
}
