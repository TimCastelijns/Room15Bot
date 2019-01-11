package com.timcastelijns.room15bot.data.db

import com.timcastelijns.room15bot.data.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserProfileDao {

    fun create(user: User, nickname: String, age: Int) {
        transaction {
            val id = UserProfiles.insert {
                it[UserProfiles.nickname] = nickname
                it[UserProfiles.age] = age
            } get UserProfiles.id

            // Assign to user.
            Users.update({ Users.id eq user.id }) {
                it[profileId] = id
            }
        }
    }

}
