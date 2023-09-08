package com.example.chatapplication.Utils

import com.example.chatapplication.ApiResponses.LoginResponse.LoginResponse
import com.example.chatapplication.ApiResponses.ChannelApiResponse.ChannelResponse
import com.example.chatapplication.ApiResponses.ChannelApiResponse.CreateChannelResponse
import com.example.chatapplication.ApiResponses.ConnectiionApiResponse.ConnectionStatus
import com.example.chatapplication.ApiResponses.ImageApiResponse.ImageResponse
import com.example.chatapplication.ApiResponses.MessageResponse.Message
import com.example.chatapplication.ApiResponses.MessageResponse.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @GET("conn.php")
    fun connect(): Call<ConnectionStatus>

    @POST("login.php")
    fun login(
        @Body body:MutableMap<String,Any>
    ): Call<LoginResponse>

    @POST("register.php")
    fun register(
        @Body body:MutableMap<String,Any>
    ): Call<ConnectionStatus>

    @POST("searchUser.php")
    fun searchUser(
        @Body body:MutableMap<String,Any>
    ): Call<LoginResponse>

    @POST("createChannel.php")
    fun createChannel(
        @Body body:MutableMap<String,Any>
    ): Call<CreateChannelResponse>

    @POST("searchChannels.php")
    fun searchChannels(
        @Body body:MutableMap<String,Any>
    ): Call<ChannelResponse>

    @POST("deleteChannel.php")
    fun deleteChannel(
        @Body body:MutableMap<String,Any>
    ): Call<Void>

    @Multipart // This annotation is used for file uploads
    @POST("uploadImage.php")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("userId") userId: RequestBody
    ): Call<Void>

    @POST("getImage.php")
    fun getImage(
        @Body body:MutableMap<String,Any>
    ): Call<ImageResponse>

    @POST("sendMessage.php")
    fun sendMessage(@Body body: Message): Call<MessageResponse>

    @POST("retrieveMessages.php")
    fun retrieveMessages(
        @Body body:MutableMap<String,Any>
    ): Call<MessageResponse>

    @POST("deleteMessage.php")
    fun deleteMessage(
        @Body body:MutableMap<String,Any>
    ): Call<Void>

}