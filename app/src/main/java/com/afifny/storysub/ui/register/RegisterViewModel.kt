package com.afifny.storysub.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afifny.storysub.api.ApiConfig
import com.afifny.storysub.model.UserResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoad

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun userRegister(name: String, email: String, password: String) {
        _isLoad.value = true
        val client = ApiConfig.getApiService().userRegister(name, email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoad.value = false
                if (response.isSuccessful) {
                    _error.value = response.body()?.error
                    _message.value = response.body()?.message.toString()
                    Log.d("TAG", "onResponse: ${response.body()?.message.toString()} ${response.body()?.error}")
                }  else {
                    val bodyError = response.errorBody()?.string()
                    val jsonObject = JSONObject(bodyError.toString())
                    val message = jsonObject.get("message")
                    val error =  jsonObject.get("error")

                    _error.value = error as Boolean?
                    _message.value = message.toString()
                    Log.d("TAG", "onResponse: ${_message.value}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoad.value = false
                Log.e("TAG", "onFailure: ${t.message.toString()}")
            }
        })
    }
}