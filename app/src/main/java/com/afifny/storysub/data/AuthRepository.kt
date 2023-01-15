package com.afifny.storysub.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.afifny.storysub.data.remote.response.UserResponse
import com.afifny.storysub.data.remote.retrofit.ApiService
import com.afifny.storysub.utils.stringSuspending
import org.json.JSONObject

class AuthRepository(
    private val apiService: ApiService,
) {
    fun userRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<UserResponse?>> = liveData {
        emit(Result.Loading)
        try {
            val returnValue = MutableLiveData<Result<UserResponse?>>()
            val response = apiService.userRegister(name, email, password)
            if (response.isSuccessful) {
                returnValue.value = Result.Success(response.body())
                emitSource(returnValue)
                Log.d(TAG, "userRegister: $returnValue")
            } else {
                val error = response.errorBody()?.stringSuspending()
                val jsonObject = JSONObject(error.toString())
                val message = jsonObject.get("message")
                returnValue.value = Result.Error(message.toString())
                emitSource(returnValue)
            }
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun userLogin(email: String, password: String): LiveData<Result<UserResponse?>> = liveData {
        emit(Result.Loading)
        try {
            val returnValue = MutableLiveData<Result<UserResponse?>>()
            val response = apiService.userLogin(email, password)
            if (response.isSuccessful) {
                returnValue.value = Result.Success(response.body())
                emitSource(returnValue)
            } else {
                val error = response.errorBody()?.stringSuspending()
                val jsonObject = JSONObject(error.toString())
                val message = jsonObject.get("message")
                returnValue.value = Result.Error(message.toString())
                emitSource(returnValue)
            }
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    companion object {
        private var TAG = "authRepository"
    }
}

