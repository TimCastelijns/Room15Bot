package com.timcastelijns.room15bot.bot.monitors

import com.timcastelijns.room15bot.bot.Actor
import com.timcastelijns.room15bot.data.db.ReminderDao
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ReminderMonitor(
        private val reminderDao: ReminderDao
) : Monitor {

    override fun start(actor: Actor): Disposable = Observable.interval(1, TimeUnit.MINUTES)
            .observeOn(Schedulers.io())
            .subscribe {
                reminderDao.getMessageIdsToRemind()
                        .forEach {
                            actor.acceptReply("Here is your reminder", it)
                        }

                reminderDao.completePassedReminders()
            }

}
