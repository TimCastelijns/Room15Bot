package com.timcastelijns.room15bot.data.db

import com.timcastelijns.room15bot.data.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Database {

    private val tables = listOf(
            Users,
            UserProfiles,
            StarredMessages,
            Reminders
    )

    fun connect(config: DatabaseConfig, andInitialize: Boolean = true) {
        Database.connect(
                config.url,
                config.driver,
                config.user,
                config.password
        )

        if (andInitialize) {
            initialize()
        }
    }

    fun initialize() = createTables()

    private fun createTables() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(*tables.toTypedArray())
        }
    }

    fun wipe() = dropTables()

    private fun dropTables() {
        transaction {
            SchemaUtils.drop(*tables.toTypedArray())
        }
    }

}
