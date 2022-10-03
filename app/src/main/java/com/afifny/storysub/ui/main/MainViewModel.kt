package com.afifny.storysub.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.afifny.storysub.api.ApiConfig
import com.afifny.storysub.model.ListStoryItem
import com.afifny.storysub.model.LoginResult
import com.afifny.storysub.model.StoryResponse
import com.afifny.storysub.model.UserPref
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPref): ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> = _isLoad

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    fun getListStory(auth: String) {
        _isLoad.value = true
        val client = ApiConfig.getApiService().getAllStory("Bearer $auth", size = 30)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoad.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                    Log.d("TAG", "onResponse: ${response.body()?.listStory}")
                } else {
                    val bodyError = response.errorBody()?.string()
                    val jsonObject = JSONObject(bodyError.toString())
                    val error = jsonObject.get("error")
                    _error.value = error as Boolean?
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoad.value = false
                Log.d("TAG", "onFailure: ${t.message}")
            }

        })
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
