package data.commands

import data.db.Reminders
import io.reactivex.Single
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import util.FutureDateExpressionParser
import java.time.Instant

class SetReminderCommand {

    fun execute(messageId: Long, command: String): Single<Instant> {
        val now = Instant.now()
        val futureDateMillis = FutureDateExpressionParser().parse(command)
        val triggerAt = now.plusMillis(futureDateMillis)

        transaction {
            Reminders.insert {
                it[Reminders.messageId] = messageId
                it[Reminders.triggerAt] = triggerAt.toEpochMilli()
                it[Reminders.completed] = false
            }
        }

        return Single.just(triggerAt)
    }


}
