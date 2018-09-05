package com.timcastelijns.room15bot.bot.monitors

import com.timcastelijns.room15bot.bot.Actor
import io.reactivex.disposables.Disposable

interface Monitor {

    fun start(actor: Actor): Disposable

}
