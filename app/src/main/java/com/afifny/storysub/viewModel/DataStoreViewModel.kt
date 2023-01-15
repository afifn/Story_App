package com.afifny.storysub.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.data.remote.response.LoginResult
import kotlinx.coroutines.launch

class DataStoreViewModel(private val pref: UserPref) : ViewModel() {
    fun login(user: LoginResult) {
        viewModelScope.launch {
            pref.saveUserLogin(user)
        }
    }

    fun getUserLogin(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}