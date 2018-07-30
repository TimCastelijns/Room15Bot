package data.commands

import data.db.StarredMessages
import data.repositories.StarredMessage
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class GetStarsDataCommand : SingleCommand<String?, StarsData> {

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

    private fun getForUsername(username: String): StarsData {
        var list = emptyList<StarredMessage>()
        var totalStarredMessages = 0
        var totalStars = 0

        transaction {
            list = StarredMessages.select { StarredMessages.username like "%$username%" }
                    .orderBy(StarredMessages.stars to false)
                    .limit(LIMIT_USER)
                    .map { it.toStarredMessage() }

            totalStarredMessages = StarredMessages
                    .select { StarredMessages.username like "%$username%" }
                    .count()

            totalStars = StarredMessages.slice(StarredMessages.stars)
                    .select { StarredMessages.username like "%$username%" }
                    .sumBy { it[StarredMessages.stars] }


        }
        return StarsData(list, totalStarredMessages, totalStars)
    }

    private fun getAny(): StarsData {
        var list = emptyList<StarredMessage>()
        var totalStarredMessages = 0
        var totalStars = 0

        transaction {
            list = StarredMessages.selectAll()
                    .orderBy(StarredMessages.stars to false)
                    .limit(LIMIT_ANY)
                    .map { it.toStarredMessage() }

            totalStarredMessages = StarredMessages.selectAll()
                    .count()

            totalStars = StarredMessages.slice(StarredMessages.stars)
                    .selectAll()
                    .sumBy { it[StarredMessages.stars] }
        }
        return StarsData(list, totalStarredMessages, totalStars)
    }

}

private fun ResultRow.toStarredMessage() = with(this) {
    StarredMessage(
            this[StarredMessages.username],
            this[StarredMessages.message],
            this[StarredMessages.stars],
            this[StarredMessages.permalink]
    )
}

data class StarsData(
        val starredMessages: List<StarredMessage>,
        val totalStarredMessages: Int,
        val totalStars: Int
)
