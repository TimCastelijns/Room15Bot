package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.ReminderDao
import com.timcastelijns.room15bot.util.FutureDateExpressionParser
import java.time.Instant

class SetReminderUseCase(
        private val ReminderDao: ReminderDao
) : UseCase<SetReminderCommandParams, Instant> {

    override fun execute(params: SetReminderCommandParams): Instant {
        val now = Instant.now()
        val futureDateMillis = FutureDateExpressionParser().parse(params.command)
        val triggerAt = now.plusMillis(futureDateMillis)

        ReminderDao.createReminder(params.messageId, triggerAt.toEpochMilli())

        return triggerAt
    }

}

data class SetReminderCommandParams(
        val messageId: Long,
        val command: String
)
