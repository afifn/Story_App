package com.afifny.storysub.data

import com.afifny.storysub.data.remote.response.StoryResponse
import com.afifny.storysub.data.remote.response.UserResponse
import com.afifny.storysub.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {
    private val dummyResponse = DataDummy.generateDummyStoryResponse()
    private val dummyResponseEmpty = DataDummy.generateDummyStoryEmptyResponse()
    private val dummyResponseAuth = DataDummy.generateDummyAuthResponse()
    override suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Response<UserResponse> {
        return dummyResponseAuth
    }

    override suspend fun userLogin(email: String, password: String): Response<UserResponse> {
        return dummyResponseAuth
    }

    override suspend fun addStory(
        auth: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Response<StoryResponse> {
        return dummyResponse
    }

    override suspend fun getAllStory(
        auth: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): Response<StoryResponse> {
        return if (auth == "Bearer token_false") dummyResponseEmpty
        else dummyResponse
    }
}