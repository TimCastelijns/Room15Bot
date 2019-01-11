package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserProfileDao {

    fun create(userId: Long, username: String) {
        transaction {
            UserProfiles.insert {
                it[UserProfiles.userId] = userId
                it[UserProfiles.username] = username
            }
        }
    }

    fun update(userId: Long, nickname: String, age: Int, location: String, skills: String, title: String) {
        transaction {
            UserProfiles.update({ UserProfiles.userId eq userId }) {
                it[UserProfiles.nickname] = nickname
                it[UserProfiles.age] = age
                it[UserProfiles.location] = location
                it[UserProfiles.skills] = skills
                it[UserProfiles.title] = title
            }
        }
    }

}
