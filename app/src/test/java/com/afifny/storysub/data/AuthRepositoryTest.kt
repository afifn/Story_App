package com.afifny.storysub.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afifny.storysub.data.remote.retrofit.ApiService
import com.afifny.storysub.utils.MainDispatcherRule
import com.afifny.storysub.utils.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class AuthRepositoryTest {
    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var apiService: ApiService
    private val name = "your name"
    private val email = "mail@mail.com"
    private val password = "password"

    @Before
    fun setup() {
        apiService = FakeApiService()
        authRepository = AuthRepository(apiService)
    }

    @Test
    fun `when user register is success`() = runTest {
        val expectedResponse = DataDummy.generateDummyAuthResponse()
        val actualValue = authRepository.userRegister(name, email, password)

        actualValue.observeForTesting {
            assertNotNull(actualValue)
            assertTrue(actualValue.value is Result.Success)
            assertEquals(expectedResponse.body()?.message, (actualValue.value as Result.Success).data?.message)
        }
    }

    @Test
    fun `when user login is success`() = runTest {
        val expectedResponse = DataDummy.generateDummyAuthResponse()
        val actualValue = authRepository.userLogin(email, password)

        actualValue.observeForTesting {
            assertNotNull(actualValue)
            assertTrue(actualValue.value is Result.Success)
            assertEquals(expectedResponse.body()?.message, (actualValue.value as Result.Success).data?.message)
        }
    }
}