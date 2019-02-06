package com.timcastelijns.room15bot.data.db

import com.timcastelijns.room15bot.data.User
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

}

private fun ResultRow.toUser() = with(this) {
    User(
            this[Users.id],
            this[Users.name]
    )
}