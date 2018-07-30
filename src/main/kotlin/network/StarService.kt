package network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface StarService {

    @GET("rooms/info/15/android?tab=stars&")
    fun getStarsDataByPage(@Query("page") page: Int) : Single<String>

}
