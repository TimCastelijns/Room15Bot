package com.timcastelijns.room15bot.data.db

import com.timcastelijns.room15bot.data.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserDao {

    fun create(id: Long, name: String? = null) {
        transaction {
            Users.insert {
                it[_id] = id
                it[Users.name] = name
            }
        }
    }

    fun getById(id: Long): User? = transaction {
        Users.select { Users._id eq id }
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
            this[Users._id],
            this[Users.name]
    )
}