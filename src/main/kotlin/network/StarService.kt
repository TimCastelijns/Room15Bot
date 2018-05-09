package network

import io.reactivex.Single
import retrofit2.http.GET

interface StarService {

    @GET("rooms/info/15/android?tab=stars&page=1")
    fun getStarsData() : Single<String>
}
