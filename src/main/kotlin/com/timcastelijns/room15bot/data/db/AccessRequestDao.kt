package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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

    fun updateProcessed(userId: Long, processed: Boolean, processedBy: String, granted: Boolean) {
        transaction {
            val existing = AccessRequests.select { (AccessRequests.userId eq userId) and (AccessRequests.processed eq false) }
                    .firstOrNull()
                    ?.toAccessRequest()

            existing?.let { _ ->
                AccessRequests.update({ AccessRequests.id eq existing.id }) {
                    it[this.processed] = processed
                    it[this.processedBy] = processedBy
                    it[this.processedAt] = Instant.now().toEpochMilli()
                    it[this.granted] = granted
                }
            }
        }
    }

    fun updateShouldMonitor(userId: Long, shouldMonitor: Boolean) {
        transaction {
            val existing = AccessRequests.select { (AccessRequests.userId eq userId) and (AccessRequests.processed eq true) }
                    .firstOrNull()
                    ?.toAccessRequest()

            existing?.let { _ ->
                AccessRequests.update({ AccessRequests.id eq existing.id }) {
                    it[this.shouldMonitor] = shouldMonitor
                }
            }
        }
    }

    fun updateRevoked(userId: Long, revoked: Boolean) {
        transaction {
            val existing = AccessRequests.select { (AccessRequests.userId eq userId) and (AccessRequests.granted eq true) }
                    .firstOrNull()
                    ?.toAccessRequest()

            existing?.let { _ ->
                AccessRequests.update({ AccessRequests.id eq existing.id }) {
                    it[this.revoked] = revoked
                }
            }
        }
    }

    fun getRecentGrantsToMonitor(): List<AccessRequest> {
        var list = emptyList<AccessRequest>()
        transaction {
            list = AccessRequests.select { AccessRequests.shouldMonitor eq true }
                    .map { it.toAccessRequest() }
        }
        return list
    }

    fun getLatestNotProcessed(): AccessRequest? {
        return transaction {
            AccessRequests.select { AccessRequests.processed eq false }
                    .orderBy(AccessRequests.createdAt to true)
                    .limit(1)
                    .map { it.toAccessRequest() }
                    .firstOrNull()
        }
    }

}

data class AccessRequest(
        val id: Int,
        val userId: Long,
        val username: String,
        val processed: Boolean,
        val processedBy: String?,
        val processedAt: Long?,
        val granted: Boolean?,
        val revoked: Boolean?,
        val shouldMonitor: Boolean
)

private fun ResultRow.toAccessRequest() = with(this) {
    AccessRequest(
            this[AccessRequests.id].value,
            this[AccessRequests.userId],
            this[AccessRequests.username],
            this[AccessRequests.processed],
            this[AccessRequests.processedBy],
            this[AccessRequests.processedAt],
            this[AccessRequests.granted],
            this[AccessRequests.revoked],
            this[AccessRequests.shouldMonitor]
    )
}
