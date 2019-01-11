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

object UserProfiles: IntIdTable() {
    val userId = integer("userId")
    val username = varchar("username", 255)
    val nickname = varchar("nickname", 255)
    val age = integer("age")
    val location = varchar("location", 255)
    val skills = varchar("skills", 255)
    val title = varchar("title", 1024)
}
