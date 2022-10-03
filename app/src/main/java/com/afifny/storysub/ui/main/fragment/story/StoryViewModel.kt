package com.afifny.storysub.ui.main.fragment.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afifny.storysub.api.ApiConfig
import com.afifny.storysub.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel: ViewModel() {
    private val _isLoad = MutableLiveData<Boolean>()
    val isLad: LiveData<Boolean> = _isLoad

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun uploadStory(token: String, description: RequestBody, photo: MultipartBody.Part, lat: Double? = null, lon: Double? = null) {
        _isLoad.value = true
        val client = ApiConfig.getApiService().addStory("Bearer $token", description, photo, lat, lon)
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoad.value = false
                if (response.isSuccessful) {
                    _error.value = response.body()?.error
                    Log.d("TAG", "onResponse: ${response.body()?.message}")
                } else {
                    val bodyError = response.errorBody()?.string()
                    val jsonObject = JSONObject(bodyError.toString())
                    val err = jsonObject.get("error")
                    _error.value = err as Boolean
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoad.value = false
                Log.e("TAG", "onFailure: ${t.message}" )
            }

        })
    }
}