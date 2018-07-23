package data.commands

import data.repositories.StarredMessageRepository
import io.reactivex.Completable
import io.reactivex.Observable

class SyncStarsDataCommand(
        private val starredMessageRepository: StarredMessageRepository
) {

    fun execute(): Completable {
        return starredMessageRepository.getNumberOfStarredMessagesPages()
                .flatMap { lastPage ->
                    Observable.range(1, lastPage)
                            .doOnNext { println(it.toString()) }
                            .flatMapSingle { pageNr -> starredMessageRepository.getStarredMessagesByPage(pageNr) }
                            .toList()
                }
                .toCompletable()

    }
}
