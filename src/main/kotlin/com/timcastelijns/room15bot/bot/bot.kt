package com.timcastelijns.room15bot.bot

import com.timcastelijns.room15bot.bot.eventhandlers.AccessLevelChangedEventHandler
import com.timcastelijns.room15bot.bot.eventhandlers.MessageEventHandler
import com.timcastelijns.room15bot.bot.monitors.ReminderMonitor
import com.timcastelijns.chatexchange.chat.ChatHost
import com.timcastelijns.chatexchange.chat.Room
import com.timcastelijns.chatexchange.chat.StackExchangeClient
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory

class Bot(
        private val accessLevelChangedEventHandler: AccessLevelChangedEventHandler,
        private val messageEventHandler: MessageEventHandler,
        private val reminderMonitor: ReminderMonitor
) : Actor {

    companion object {
        private val logger = LoggerFactory.getLogger(Bot::class.java)
    }

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    private lateinit var room: Room

    private val disposables = CompositeDisposable()

    fun observeLife(): Observable<Boolean> {
        return aliveSubject.hide()
    }

    fun boot(client: StackExchangeClient, roomId: Int) {
        aliveSubject.onNext(true)
        joinRoom(client, roomId)
    }

    private fun die() {
        disposables.clear()

        aliveSubject.onNext(false)
        aliveSubject.onComplete()
    }

    private fun joinRoom(client: StackExchangeClient, roomId: Int) {
        room = client.joinRoom(ChatHost.STACK_OVERFLOW, roomId)
    }

    fun start() {
        room.accessLevelChangedEventListener = {
            launch {
                accessLevelChangedEventHandler.handle(it, this@Bot)
            }
        }

        room.messagePostedEventListener = {
            logger.debug("${it.userName}: ${it.message.content}")
            launch {
                messageEventHandler.handle(it, this@Bot)
            }
        }

        room.messageEditedEventListener = {
            launch {
                messageEventHandler.handle(it, this@Bot)
            }
        }

        monitorReminders()
    }

    private fun monitorReminders() = disposables.add(reminderMonitor.start(room))

    override fun acceptMessage(message: String) {
        room.send(message)
    }

    override fun acceptReply(message: String, targetMessageId: Long) {
        room.replyTo(targetMessageId, message)
    }

    override fun leaveRoom() {
        die()
    }
}

interface Actor {
    fun acceptMessage(message: String)
    fun acceptReply(message: String, targetMessageId: Long)
    fun leaveRoom()
}
