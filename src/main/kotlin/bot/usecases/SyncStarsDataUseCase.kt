package bot.usecases

import data.db.StarredMessageDao
import data.repositories.StarredMessage
import data.repositories.StarredMessageRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

class SyncStarsDataUseCase(
        private val starredMessageRepository: StarredMessageRepository,
        private val starredMessageDao: StarredMessageDao
) : AsyncUseCase<Unit, Unit> {

    override suspend fun execute(params: Unit) {
        val pageCount = starredMessageRepository.getNumberOfStarredMessagesPages()

        val jobs = arrayListOf<Deferred<List<StarredMessage>>>()
        for (pageNr in 1..pageCount) {
            jobs += async {
                starredMessageRepository.getStarredMessagesByPage(pageNr)
            }
        }

        val starredMessages = mutableListOf<StarredMessage>()
        jobs.forEachIndexed { i, it ->
            starredMessages += it.await()
            println("Fetched page $i")
        }

        store(starredMessages)
    }

    private fun store(it: List<StarredMessage>) {
        it.maxBy { it.message.length }
                ?.also { println("${it.message.length} is the longest message") }

        it.chunked(500).forEach {
            starredMessageDao.create(it)
        }
    }
}

fun String.truncate(max: Int = 500) = if (length > max) substring(0, max) else this
