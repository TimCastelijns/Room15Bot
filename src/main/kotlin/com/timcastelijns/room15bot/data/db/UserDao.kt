package com.timcastelijns.room15bot.data.db

import com.timcastelijns.room15bot.data.User
import com.timcastelijns.room15bot.data.UserProfile
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserDao {

    fun create(id: Long, name: String? = null) {
        transaction {
            Users.insertIgnore {
                it[Users.id] = id
                it[Users.name] = name
            }
        }
    }

    fun getById(id: Long): User? = transaction {
        Users.select { Users.id eq id }
                .firstOrNull()
                ?.toUser()
    }

    fun getByName(name: String): List<User> = transaction {
        Users.select { Users.name eq name }
                .map { it.toUser() }
    }

    fun getProfile(user: User): UserProfile? =
            transaction {
                (Users innerJoin UserProfiles).select { Users.id eq user.id }
                        .firstOrNull()
                        ?.toUserProfile()
            }

}

private fun ResultRow.toUser() = with(this) {
    User(
            this[Users.id],
            this[Users.name]
    )
}

private fun ResultRow.toUserProfile() = with(this) {
    UserProfile(
            this[UserProfiles.id],
            this[UserProfiles.nickname],
            this[UserProfiles.age]
    )
}
