package com.example.app_oscar.conf

import com.example.app_oscar.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OscarApi {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/votes/confirm")
    fun confirmVote(@Body request: VoteRequest): Call<VoteResponse>

    @GET("http://200.236.3.97/filme.json")
    fun getFilmes(): Call<List<Filme>>

    @GET("http://200.236.3.97/diretor.json")
    fun getDiretores(): Call<List<Diretor>>

    @POST("/api/votes/status")
    fun checkVoteStatus(
        @Body body: VoteStatusRequest
    ): Call<VoteStatusResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.18.184:3000/" // IP padrão do emulador Android para localhost

    val instance: OscarApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(OscarApi::class.java)
    }
}