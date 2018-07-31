package bot.commands

import io.reactivex.Completable
import io.reactivex.Single


interface Command<in P, out T> {

    fun execute(params: P): T
}

interface CompletableCommand<in P> : Command<P, Completable>
interface SingleCommand<in P, T> : Command<P, Single<T>>
