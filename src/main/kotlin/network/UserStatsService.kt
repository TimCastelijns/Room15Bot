package network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface UserStatsService {

    @GET("users/{id}?tab=questions")
    fun getUserProfileQuestions(@Path("id") id: Long): Single<String>

    @GET("users/{id}?tab=answers")
    fun getUserProfileAnswers(@Path("id") id: Long): Single<String>

}
