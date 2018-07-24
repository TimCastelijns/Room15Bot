package data.commands

import data.db.StarredMessages
import data.repositories.StarredMessageRepository
import io.reactivex.Completable
import io.reactivex.Observable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction

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
                .doOnSuccess {
                    it.flatten().maxBy { it.message.length }?.also { println("${it.message.length} is the longest message") }

                    transaction {
                        it.chunked(500).forEach {
                            it.forEach {
                                StarredMessages.batchInsert(it) {
                                    this[StarredMessages.username] = it.username
                                    this[StarredMessages.message] = it.message.truncate()
                                    this[StarredMessages.stars] = it.stars
                                    this[StarredMessages.permalink] = it.permanentLink
                                }
                            }
                        }
                    }
                }
                .toCompletable()

    }
}

fun String.truncate(max: Int = 500) = if (length > max) substring(0, max) else this
