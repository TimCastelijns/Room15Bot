package data.db

import data.commands.truncate
import data.repositories.StarredMessage
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class StarredMessageDao {

    fun create(starredMessages: List<StarredMessage>) {
        transaction {
            StarredMessages.batchInsert(starredMessages) {
                this[StarredMessages.username] = it.username
                this[StarredMessages.message] = it.message.truncate()
                this[StarredMessages.stars] = it.stars
                this[StarredMessages.permalink] = it.permanentLink
            }
        }
    }

    fun getStarredMessages(limit: Int): List<StarredMessage> {
        var list = emptyList<StarredMessage>()
        transaction {
            list = StarredMessages.selectAll()
                    .orderBy(StarredMessages.stars to false)
                    .limit(limit)
                    .map { it.toStarredMessage() }
        }
        return list
    }

    fun getStarredMessagesForUser(username: String, limit: Int): List<StarredMessage> {
        var list = emptyList<StarredMessage>()
        transaction {
            list = StarredMessages.select { StarredMessages.username like "%$username%" }
                    .orderBy(StarredMessages.stars to false)
                    .limit(limit)
                    .map { it.toStarredMessage() }
        }
        return list
    }

    fun getMessageCount(): Int {
        var totalStarredMessages = 0
        transaction {
            totalStarredMessages = StarredMessages.selectAll()
                    .count()
        }
        return totalStarredMessages
    }

    fun getMessageCountForUser(username: String): Int {
        var totalStarredMessages = 0
        transaction {
            totalStarredMessages = StarredMessages
                    .select { StarredMessages.username like "%$username%" }
                    .count()
        }
        return totalStarredMessages
    }

    fun getStarCount(): Int {
        var totalStars = 0
        transaction {
            totalStars = StarredMessages.slice(StarredMessages.stars)
                    .selectAll()
                    .sumBy { it[StarredMessages.stars] }
        }
        return totalStars
    }

    fun getStarCountForUser(username: String): Int {
        var totalStars = 0
        transaction {
            totalStars = StarredMessages.slice(StarredMessages.stars)
                    .select { StarredMessages.username like "%$username%" }
                    .sumBy { it[StarredMessages.stars] }

        }
        return totalStars
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
