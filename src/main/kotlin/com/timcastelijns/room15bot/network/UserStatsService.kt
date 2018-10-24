package com.timcastelijns.room15bot.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface UserStatsService {

    @GET("users/{id}?tab=questions")
    fun getUserProfileQuestions(@Path("id") id: Long): Deferred<String>

    @GET("users/{id}?tab=answers")
    fun getUserProfileAnswers(@Path("id") id: Long): Deferred<String>

}
