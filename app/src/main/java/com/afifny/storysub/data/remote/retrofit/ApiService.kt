package com.afifny.storysub.data.remote.retrofit

import com.afifny.storysub.data.remote.response.StoryResponse
import com.afifny.storysub.data.remote.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<UserResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<UserResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") auth: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") latitude: RequestBody?,
        @Part("lon") longitude: RequestBody?
    ): Response<StoryResponse>

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization") auth: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): Response<StoryResponse>
}