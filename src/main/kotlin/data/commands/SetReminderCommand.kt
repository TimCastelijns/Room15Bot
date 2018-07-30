package data.commands

import data.db.Reminders
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import util.FutureDateExpressionParser
import java.time.Instant

class SetReminderCommand: SingleCommand<SetReminderCommandParams, Instant> {

    override fun execute(params: SetReminderCommandParams): Single<Instant> {
        val now = Instant.now()
        val futureDateMillis = try {
            FutureDateExpressionParser().parse(params.command)
        } catch (e: IllegalArgumentException) {
            return Single.error(e)
        }
        val triggerAt = now.plusMillis(futureDateMillis)

        transaction {
            Reminders.insert {
                it[Reminders.messageId] = params.messageId
                it[Reminders.triggerAt] = triggerAt.toEpochMilli()
                it[Reminders.completed] = false
            }
        }

        return Single.just(triggerAt)
                .subscribeOn(Schedulers.io())
    }

}

data class SetReminderCommandParams(
        val messageId: Long,
        val command: String
)
