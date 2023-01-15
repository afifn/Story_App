package com.afifny.storysub.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afifny.storysub.data.AuthRepository
import com.afifny.storysub.data.Result
import com.afifny.storysub.data.remote.response.UserResponse
import com.afifny.storysub.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: AuthRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyName = "Your name"
    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(repository)
    }

    @Test
    fun `when email and password valid, register success`(): Unit = runTest {
        val responseMessage = "success"
        val expectedResponse = UserResponse(null, false, responseMessage)
        val expectedValue = MutableLiveData<Result<UserResponse?>>()
        expectedValue.value = Result.Success(expectedResponse)

        `when`(registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedValue
        )
        val actualValue =
            registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(repository).userRegister(dummyName, dummyEmail, dummyPassword)

        assertNotNull(actualValue)
        assertTrue(actualValue is Result.Success)
        assertEquals(responseMessage, (actualValue as Result.Success).data?.message)
    }

    @Test
    fun `when userRegister failed, email is already taken`(): Unit = runTest {
        val responseMessage = "Email is already taken"
        val expectedValue = MutableLiveData<Result<UserResponse?>>()
        expectedValue.value = Result.Error(responseMessage)

        `when`(registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedValue
        )
        val actualValue =
            registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(repository).userRegister(dummyName, dummyEmail, dummyPassword)

        assertTrue(actualValue is Result.Error)
        assertEquals(responseMessage, (actualValue as Result.Error).error)
    }
}
