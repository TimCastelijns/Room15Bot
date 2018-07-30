package data.commands

import data.db.ReminderDao
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import util.FutureDateExpressionParser
import java.time.Instant

class SetReminderCommand(
    private val ReminderDao: ReminderDao
): SingleCommand<SetReminderCommandParams, Instant> {

    override fun execute(params: SetReminderCommandParams): Single<Instant> {
        val now = Instant.now()
        val futureDateMillis = try {
            FutureDateExpressionParser().parse(params.command)
        } catch (e: IllegalArgumentException) {
            return Single.error(e)
        }
        val triggerAt = now.plusMillis(futureDateMillis)

        ReminderDao.createReminder(params.messageId, triggerAt.toEpochMilli())

        return Single.just(triggerAt)
                .subscribeOn(Schedulers.io())
    }

}

data class SetReminderCommandParams(
        val messageId: Long,
        val command: String
)
