package com.timcastelijns.room15bot.data.db

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class ReminderDao {

    fun createReminder(messageId: Long, triggerAt: Long) {
        transaction {
            Reminders.insert {
                it[Reminders.messageId] = messageId
                it[Reminders.triggerAt] = triggerAt
                it[completed] = false
            }
        }
    }

    fun getMessageIdsToRemind(): List<Long> {
        val nowMillis = Instant.now().toEpochMilli()

        // Query for reminders to send out.
        val messageIdsToReplyTo = transaction {
            Reminders.select { (Reminders.triggerAt lessEq nowMillis) and (Reminders.completed eq false) }
                    .map { it[Reminders.messageId] }
        }

        return messageIdsToReplyTo
    }

    fun completePassedReminders() {
        val nowMillis = Instant.now().toEpochMilli()

        // Set reminders to completed.
        transaction {
            Reminders.update({ Reminders.triggerAt lessEq nowMillis }) {
                it[completed] = true
            }
        }
    }

}
