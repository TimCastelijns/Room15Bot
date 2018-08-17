package bot.monitors

import com.timcastelijns.chatexchange.chat.Room
import data.db.ReminderDao
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ReminderMonitor(
        private val reminderDao: ReminderDao
) {

    fun start(room: Room): Disposable = Observable.interval(1, TimeUnit.MINUTES)
            .observeOn(Schedulers.io())
            .subscribe {
                reminderDao.getMessageIdsToRemind()
                        .forEach {
                            room.replyTo(it, "Here is your reminder")
                        }

                reminderDao.completePassedReminders()
            }

}
