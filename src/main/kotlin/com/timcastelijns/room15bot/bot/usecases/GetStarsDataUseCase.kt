package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.StarsData
import com.timcastelijns.room15bot.data.db.StarredMessageDao

class GetStarsDataUseCase(
        private val starredMessageDao: StarredMessageDao
) : UseCase<String?, StarsData> {

    companion object {
        private const val LIMIT_USER = 3
        private const val LIMIT_ANY = 25
        private const val LIMIT_WEEK = 5
        private const val LIMIT_MONTH = 10
    }

    override fun execute(params: String?) =
            params?.let {
                when (it.toLowerCase()) {
                    "week" -> getForWeek()
                    "month" -> getForMonth()
                    else -> getForUsername(it)
                }
            } ?: getAny()

    private fun getForWeek() = getForRecentDays(7, LIMIT_WEEK)

    private fun getForMonth() = getForRecentDays(30, LIMIT_MONTH)

    private fun getForRecentDays(days: Int, limit: Int): StarsData {
        val starredMessages = starredMessageDao.getRecentStarredMessages(days, limit)
        return StarsData(starredMessages, starredMessages.size, starredMessages.sumBy { it.stars })
    }

    private fun getForUsername(username: String) =
            StarsData(starredMessageDao.getStarredMessagesForUser(username, LIMIT_USER),
                    starredMessageDao.getMessageCountForUser(username),
                    starredMessageDao.getStarCountForUser(username))

    private fun getAny() =
            StarsData(starredMessageDao.getStarredMessages(LIMIT_ANY),
                    starredMessageDao.getMessageCount(),
                    starredMessageDao.getStarCount())

}
