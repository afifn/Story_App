package com.afifny.storysub.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afifny.storysub.api.ApiConfig
import com.afifny.storysub.model.LoginResult
import com.afifny.storysub.model.UserPref
import com.afifny.storysub.model.UserResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPref) : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoad

    private val _users = MutableLiveData<LoginResult>()
    val users: LiveData<LoginResult> = _users

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun userLogin(email: String, password: String) {
        _isLoad.value = true
        val client = ApiConfig.getApiService().userLogin(email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoad.value = false
                if (response.isSuccessful) {
                    _message.value = response.body()?.message.toString()
                    _users.value = response.body()?.loginResult
                    _error.value = response.body()?.error
                    Log.d("TAG", "onResponse: ${response.body()?.message.toString()}")
                } else {
                    val bodyError = response.errorBody()?.string()
                    val jsonObject = JSONObject(bodyError.toString())
                    val message = jsonObject.get("message")
                    val error = jsonObject.get("error")

                    _message.value = message.toString()
                    _error.value = error as Boolean
                    Log.d("TAG", "onResponse: ${_message.value}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoad.value = false
                _message.value = t.message.toString()
                Log.e("TAG", "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun login(user: LoginResult) {
        viewModelScope.launch {
            pref.saveUserLogin(user)
        }
    }
}