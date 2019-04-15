package com.timcastelijns.room15bot.data.db

import com.timcastelijns.room15bot.bot.usecases.truncate
import com.timcastelijns.room15bot.data.StarredMessage
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class StarredMessageDao {

    fun create(starredMessages: List<StarredMessage>) {
        transaction {
            StarredMessages.batchInsert(starredMessages) {
                this[StarredMessages.username] = it.username
                this[StarredMessages.message] = it.message.truncate()
                this[StarredMessages.stars] = it.stars
                this[StarredMessages.permalink] = it.permanentLink
                this[StarredMessages.date] = it.date
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

    fun getRecentStarredMessages(days: Int, limit: Int): List<StarredMessage> {
        val then = DateTime.now(DateTimeZone.UTC).minusDays(days)
        var list = emptyList<StarredMessage>()
        transaction {
            list = StarredMessages.select { StarredMessages.date.greaterEq(then) }
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

    fun getTopStarredMessageWithAge(age: Int): StarredMessage? {
        val then = DateTime.now(DateTimeZone.UTC).minusDays(age)
        var message: StarredMessage? = null
        transaction {
            message = StarredMessages.select { StarredMessages.date.eq(then)}
                    .orderBy(StarredMessages.stars to SortOrder.DESC)
                    .limit(1)
                    .firstOrNull()
                    ?.toStarredMessage()
        }
        return message
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

    fun deleteAll() {
        transaction {
            if (StarredMessages.exists()) {
                StarredMessages.deleteAll()
            }
        }
    }

}

private fun ResultRow.toStarredMessage() = with(this) {
    StarredMessage(
            this[StarredMessages.username],
            this[StarredMessages.message],
            this[StarredMessages.stars],
            this[StarredMessages.permalink],
            this[StarredMessages.date]
    )
}
