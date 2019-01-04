package com.timcastelijns.room15bot.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface StarService {

    @GET("rooms/info/15/android?tab=stars&")
    fun getStarsDataByPage(@Query("page") page: Int) : Deferred<String>

}
