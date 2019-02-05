package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.StarsData
import com.timcastelijns.room15bot.data.db.StarredMessageDao

class GetStarsDataUseCase(
        private val starredMessageDao: StarredMessageDao
) : UseCase<String?, StarsData> {

    companion object {
        private const val LIMIT_USER = 3
        private const val LIMIT_ANY = 25
    }

    override fun execute(params: String?) =
            if (params != null) {
                getForUsername(params)
            } else {
                getAny()
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
