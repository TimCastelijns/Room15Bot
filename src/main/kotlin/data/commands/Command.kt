package data.commands

import io.reactivex.Completable
import io.reactivex.Single


interface Command<P, T> {

    fun execute(params: P): T
}

interface CompletableCommand<P> : Command<P, Completable>
interface SingleCommand<P, T> : Command<P, Single<T>>
