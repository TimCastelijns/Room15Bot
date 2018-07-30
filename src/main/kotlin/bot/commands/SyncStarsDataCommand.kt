package bot.commands

import data.db.StarredMessageDao
import data.repositories.StarredMessage
import data.repositories.StarredMessageRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SyncStarsDataCommand(
        private val starredMessageRepository: StarredMessageRepository,
        private val starredMessageDao: StarredMessageDao
) : CompletableCommand<Unit> {

    override fun execute(params: Unit): Completable = starredMessageRepository.getNumberOfStarredMessagesPages()
            .flatMap { lastPage ->
                Observable.range(1, lastPage)
                        .doOnNext { println(it.toString()) }
                        .flatMapSingle { pageNr -> starredMessageRepository.getStarredMessagesByPage(pageNr) }
                        .toList()
            }
            .doOnSuccess { store(it) }
            .toCompletable()
            .subscribeOn(Schedulers.io())

    private fun store(it: MutableList<List<StarredMessage>>) {
        it.flatten().maxBy { it.message.length }
                ?.also { println("${it.message.length} is the longest message") }

        it.chunked(500).forEach {
            it.forEach {
                starredMessageDao.create(it)
            }
        }
    }
}

fun String.truncate(max: Int = 500) = if (length > max) substring(0, max) else this
