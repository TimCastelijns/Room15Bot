package bot.usecases

import io.reactivex.Completable
import io.reactivex.Single


interface UseCase<in P, out T> {

    fun execute(params: P): T
}

interface CompletableUseCase<in P> : UseCase<P, Completable>
interface SingleUseCase<in P, T> : UseCase<P, Single<T>>

interface AsyncUseCase<in P, out T> {

    suspend fun execute(params: P): T

}
