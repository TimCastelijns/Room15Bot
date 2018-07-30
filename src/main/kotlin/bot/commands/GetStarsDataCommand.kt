package bot.commands

import data.db.StarredMessageDao
import data.repositories.StarredMessage
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class GetStarsDataCommand(
        private val starredMessageDao: StarredMessageDao
) : SingleCommand<String?, StarsData> {

    companion object {
        private const val LIMIT_USER = 3
        private const val LIMIT_ANY = 25
    }

    override fun execute(params: String?): Single<StarsData> {
        val starredMessages = if (params != null) {
            getForUsername(params)
        } else {
            getAny()
        }

        return Single.just(starredMessages)
                .subscribeOn(Schedulers.io())
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

data class StarsData(
        val starredMessages: List<StarredMessage>,
        val totalStarredMessages: Int,
        val totalStars: Int
)
