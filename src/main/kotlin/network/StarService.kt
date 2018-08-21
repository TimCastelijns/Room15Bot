package network

import io.reactivex.Single
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface StarService {

    @GET("rooms/info/15/android?tab=stars&")
    fun getStarsDataByPage(@Query("page") page: Int) : Deferred<String>

}
