package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.dao.LongIdTable

object Users : LongIdTable() {
    //    override val id: Column<EntityID<Long>> = long("id").primaryKey().entityId()
    val _id = long("_id").primaryKey()
    val name = varchar("name", 255).nullable()
}

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
