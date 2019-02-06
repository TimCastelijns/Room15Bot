package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import org.joda.time.DateTime

object Users : Table() {
    val id = long("id").primaryKey()
    val name = varchar("name", 255).nullable()
    val createdAt = datetime("createdAt").defaultExpression(CurrentDateTimeUtc())
    val profileId = (integer("profileId") references UserProfiles.id).nullable()
}

object UserProfiles: Table() {
    val id = integer("id").autoIncrement().primaryKey()
//    val userId = integer("userId")
//    val username = varchar("username", 255)
    val nickname = varchar("nickname", 255).nullable()
    val age = integer("age").nullable()
//    val location = varchar("location", 255)
//    val skills = varchar("skills", 255)
//    val title = varchar("title", 1024)
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

/**
 * Built in [CurrentDateTime] uses the machine's local datetime. We want UTC.
 */
private class CurrentDateTimeUtc : Function<DateTime>(DateColumnType(false)) {
    override fun toSQL(queryBuilder: QueryBuilder) = "UTC_TIMESTAMP()"
}
