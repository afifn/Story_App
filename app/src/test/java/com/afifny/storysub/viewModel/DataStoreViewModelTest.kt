package com.afifny.storysub.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.data.remote.response.LoginResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DataStoreViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var pref: UserPref
    private lateinit var viewModel: DataStoreViewModel

    private val name = "your name"
    private val userId = "id-1"
    private val token = "token-1"
    private val loginResult = LoginResult(name, userId, token)

    @Before
    fun setUp() {
        viewModel = DataStoreViewModel(pref)
    }

    @Test
    fun `save auth token success`(): Unit = runTest {
        pref.saveUserLogin(loginResult)
        Mockito.verify(pref).saveUserLogin(loginResult)
    }

    @Test
    fun `get user data`(): Unit = runTest {
        pref.getUser()
        Mockito.verify(pref).getUser()
    }

    @Test
    fun `user logout`(): Unit = runTest {
        pref.logout()
        Mockito.verify(pref).logout()
    }
}