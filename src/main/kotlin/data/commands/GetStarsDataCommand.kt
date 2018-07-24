package data.commands

import data.db.StarredMessages
import data.repositories.StarredMessage
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class GetStarsDataCommand {

    companion object {
        private const val LIMIT_USER = 3
        private const val LIMIT_ANY = 25
    }

    fun execute(username: String?): Single<List<StarredMessage>> {
        val starredMessages = if (username != null) {
            getForUsername(username)
        } else {
            getAny()
        }

        return Single.just(starredMessages)
                .subscribeOn(Schedulers.io())
    }

    private fun getForUsername(username: String): List<StarredMessage> {
        var list = emptyList<StarredMessage>()
        transaction {
            list = StarredMessages.select { StarredMessages.username like "%$username%" }
                    .orderBy(StarredMessages.stars to false)
                    .limit(LIMIT_USER)
                    .map { it.toStarredMessage() }
        }
        return list
    }

    private fun getAny(): List<StarredMessage> {
        var list = emptyList<StarredMessage>()
        transaction {
            list = StarredMessages.selectAll()
                    .orderBy(StarredMessages.stars to false)
                    .limit(LIMIT_ANY)
                    .map { it.toStarredMessage() }
        }
        return list
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
