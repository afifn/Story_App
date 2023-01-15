package com.afifny.storysub.ui.register

import androidx.lifecycle.ViewModel
import com.afifny.storysub.data.AuthRepository

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    fun userRegister(name: String, email: String, password: String) =
        repository.userRegister(name, email, password)
}