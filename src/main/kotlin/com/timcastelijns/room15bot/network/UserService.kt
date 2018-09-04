package com.timcastelijns.room15bot.network

import com.google.gson.JsonObject
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {

    @FormUrlEncoded
    @POST("user/info")
    fun getUser(@Field("ids") ids: String,
                @Field("roomId") roomId: String = "15"): Deferred<JsonObject>

}
