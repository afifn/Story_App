package com.afifny.storysub.ui.login

import androidx.lifecycle.ViewModel
import com.afifny.storysub.data.AuthRepository

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    fun userLogin(email: String, password: String) = repository.userLogin(email, password)
}