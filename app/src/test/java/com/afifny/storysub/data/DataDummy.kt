package com.afifny.storysub.data

import com.afifny.storysub.data.remote.response.ListStoryItem
import com.afifny.storysub.data.remote.response.LoginResult
import com.afifny.storysub.data.remote.response.StoryResponse
import com.afifny.storysub.data.remote.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

object DataDummy {
    private const val dummyToken = "token"
    private const val dummyName = "your name"
    private const val dummyId = "id"
    fun generateDummyStoryEntity(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 1..10) {
            val story = ListStoryItem(
                "url",
                "2022-01-01T06:34:18.598Z",
                "Afif $i",
                "Trisno ke $i",
                11.111,
                "id-$i",
                -12.13,
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

    fun generateDummyStoryResponse(): Response<StoryResponse> {
        return Response.success(
            StoryResponse(
                generateDummyStoryEntity(),
                false,
                "success"
            )
        )
    }

    fun generateDummyStoryEmptyResponse(): Response<StoryResponse> {
        return Response.success(
            StoryResponse(listOf(), false, "succes")
        )
    }

    fun generateDummyAuthResponse(): Response<UserResponse> {
        return Response.success(
            UserResponse(
                LoginResult(
                    dummyName,
                    dummyId,
                    dummyToken
                ),
                false,
                "success"
            )
        )
    }

}