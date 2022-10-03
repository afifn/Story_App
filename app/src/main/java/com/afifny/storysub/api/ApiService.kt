package com.afifny.storysub.api

import com.afifny.storysub.model.StoryResponse
import com.afifny.storysub.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ):Call<UserResponse>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ):Call<UserResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") auth: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") latitude: Double?,
        @Part("lon") longitude: Double?
    ):Call<StoryResponse>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization") auth: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ):Call<StoryResponse>
}