package data.db

import data.repositories.ConfigRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Database(
        private val configRepository: ConfigRepository
) {

    fun connect() {
        val config = configRepository.getDatabaseConfig()
        Database.connect(
                config.url,
                config.driver,
                config.user,
                config.password
        )

        createTables()
    }

    private fun createTables() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                    StarredMessages,
                    Reminders
            )
        }
    }

}
