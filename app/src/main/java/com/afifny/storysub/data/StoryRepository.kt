package com.afifny.storysub.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.afifny.storysub.data.local.preference.UserPref
import com.afifny.storysub.data.local.room.StoryDatabase
import com.afifny.storysub.data.remote.StoryRemoteMediator
import com.afifny.storysub.data.remote.response.ListStoryItem
import com.afifny.storysub.data.remote.response.StoryResponse
import com.afifny.storysub.data.remote.retrofit.ApiService
import com.afifny.storysub.utils.stringSuspending
import com.afifny.storysub.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val pref: UserPref
) {
    fun getStoryList(): LiveData<PagingData<ListStoryItem>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(
                    database = storyDatabase,
                    apiService = apiService,
                    pref = pref
                ),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStory()
                }
            ).liveData
        }
    }

    fun getStoryLocation(token: String): LiveData<Result<StoryResponse?>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val returnValue = MutableLiveData<Result<StoryResponse?>>()
                val response = apiService.getAllStory("Bearer $token", location = 1)
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
    }

    fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<Result<StoryResponse?>> = liveData {
        emit(Result.Loading)
        try {
            val returnValue = MutableLiveData<Result<StoryResponse?>>()
            val response =
                apiService.addStory("Bearer $token", description, photo, lat, lon)
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
}

