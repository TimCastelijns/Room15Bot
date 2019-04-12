package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.StarredMessage
import com.timcastelijns.room15bot.data.db.StarredMessageDao

class GetTopMessageUseCase(
        private val starredMessageDao: StarredMessageDao
) : UseCase<Int, StarredMessage?> {

    override fun execute(params: Int): StarredMessage? {
        return starredMessageDao.getTopStarredMessageWithAge(params)
    }

}
