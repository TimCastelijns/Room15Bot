package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.dao.LongIdTable
import java.time.Instant

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
    val processedBy = varchar("processedBy", 255).nullable()
    val timestamp = long("timestamp").default(Instant.now().toEpochMilli())
}
