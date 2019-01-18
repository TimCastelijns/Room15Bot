package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.dao.LongIdTable

object StarredMessages : LongIdTable() {
    val username = varchar("username", 255)
    val message = varchar("message", 500)
    val stars = integer("stars")
    val permalink = varchar("permalink", 255)
}

object Reminders : IntIdTable() {
    val messageId = long("messageId")
    val triggerAt = long("triggerAt")
    val completed = bool("completed").default(false)
}

object AccessRequests: IntIdTable() {
    val userId = long("userId")
    val username = varchar("username", 255)
    val processed = bool("processed").default(false)
    val processedAt = long("processedAt").nullable()
    val processedBy = varchar("processedBy", 255).nullable()
    val createdAt = long("createdAt")
    val granted = bool("granted").nullable()
    val revoked = bool("revoked").nullable()
    val shouldMonitor = bool("shouldMonitor").default(false)
}
