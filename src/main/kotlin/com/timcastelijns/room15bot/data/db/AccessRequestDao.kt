package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class AccessRequestDao {

    fun create(userId: Long, username: String) {
        transaction {
            AccessRequests.insert {
                it[this.userId] = userId
                it[this.username] = username
                it[this.createdAt] = Instant.now().toEpochMilli()
            }
        }
    }

    fun update(userId: Long, processed: Boolean, processedBy: String, accessGranted: Boolean) {
        transaction {
            AccessRequests.update({ AccessRequests.userId eq userId }) {
                it[this.processed] = processed
                it[this.processedBy] = processedBy
                it[this.processedAt] = Instant.now().toEpochMilli()
                it[this.accessGranted] = accessGranted
            }
        }
    }

}
