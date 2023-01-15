package com.afifny.storysub.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afifny.storysub.data.AuthRepository
import com.afifny.storysub.data.Result
import com.afifny.storysub.data.remote.response.LoginResult
import com.afifny.storysub.data.remote.response.UserResponse
import com.afifny.storysub.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"
    private val name = "your name"
    private val userId = "id-1"
    private val token = "token-1"
    private val loginResult = LoginResult(name, userId, token)

    @Before
    fun setUp() {
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun `when Login is successfully`(): Unit = runTest {
        val responseMessage = "success"
        val expectedResponse = UserResponse(loginResult, false, responseMessage)
        val expectedValue = MutableLiveData<Result<UserResponse?>>()
        expectedValue.value = Result.Success(expectedResponse)

        `when`(viewModel.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedValue)
        val actualValue = viewModel.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(repository).userLogin(dummyEmail, dummyPassword)

        Assert.assertNotNull(actualValue)
        Assert.assertTrue(actualValue is Result.Success)
        Assert.assertEquals(responseMessage, (actualValue as Result.Success).data?.message)
    }

    @Test
    fun `when Login is failed`(): Unit = runTest {
        val responseMessage = "login failed"
        val expectedResponse = UserResponse(loginResult, true, responseMessage)
        val expectedValue = MutableLiveData<Result<UserResponse?>>()
        expectedValue.value = Result.Error(expectedResponse.message)

        `when`(viewModel.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedValue)
        val actualValue = viewModel.userLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(repository).userLogin(dummyEmail, dummyPassword)

        Assert.assertTrue(actualValue is Result.Error)
        Assert.assertEquals(responseMessage, (actualValue as Result.Error).error)
    }
}