package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.StarredMessageDao
import com.timcastelijns.room15bot.data.repositories.StarredMessage
import com.timcastelijns.room15bot.data.repositories.StarredMessageRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.slf4j.LoggerFactory

class SyncStarsDataUseCase(
        private val starredMessageRepository: StarredMessageRepository,
        private val starredMessageDao: StarredMessageDao
) : AsyncUseCase<Unit, Unit> {

    companion object {
        private val logger = LoggerFactory.getLogger(SyncStarsDataUseCase::class.java)
    }

    override suspend fun execute(params: Unit) {
        logger.debug("starting execution")
        val pageCount = starredMessageRepository.getNumberOfStarredMessagesPages()
        logger.debug("pagecount: $pageCount")

        val jobs = arrayListOf<Deferred<List<StarredMessage>>>()
        for (pageNr in 1..pageCount) {
            jobs += async {
                starredMessageRepository.getStarredMessagesByPage(pageNr)
            }
        }
        logger.debug("started ${jobs.size} jobs")

        val starredMessages = mutableListOf<StarredMessage>()
        jobs.forEachIndexed { i, it ->
            starredMessages += it.await()

            if (i != 0 && i % 50 == 0) {
                logger.debug("fetched 50 pages")
            } else if (jobs.lastIndex == i) {
                logger.debug("fetched ${i % 50 + 1} pages")
            }
        }
        logger.debug("fetched ${jobs.size} pages in total")

        starredMessageDao.deleteAll()
        store(starredMessages)
    }

    private fun store(it: List<StarredMessage>) {
        it.maxBy { it.message.length }
                ?.also { println("${it.message.length} is the longest message") }

        it.chunked(500).forEach {
            starredMessageDao.create(it)
        }

        logger.debug("stored ${it.size} starred messages")
    }
}

fun String.truncate(max: Int = 500) = if (length > max) substring(0, max) else this
