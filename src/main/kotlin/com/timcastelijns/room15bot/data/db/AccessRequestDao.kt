package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class AccessRequestDao {

    fun create(userId: Long, username: String) {
        transaction {
            AccessRequests.insert {
                it[this.userId] = userId
                it[this.username] = username
            }
        }
    }

}
