package com.timcastelijns.room15bot.bot

import com.timcastelijns.chatexchange.chat.*
import com.timcastelijns.room15bot.bot.eventhandlers.AccessLevelChangedEventHandler
import com.timcastelijns.room15bot.bot.eventhandlers.MessageEventHandler
import com.timcastelijns.room15bot.bot.monitors.ReminderMonitor
import com.timcastelijns.room15bot.bot.usecases.GetBuildConfigUseCase
import com.timcastelijns.room15bot.bot.usecases.truncate
import com.timcastelijns.room15bot.util.MessageFormatter
import com.timcastelijns.room15bot.util.sanitize
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class Bot(
        private val accessLevelChangedEventHandler: AccessLevelChangedEventHandler,
        private val messageEventHandler: MessageEventHandler,
        private val reminderMonitor: ReminderMonitor,
        private val getBuildConfigUseCase: GetBuildConfigUseCase,
        private val messageFormatter: MessageFormatter
) : Actor {

    companion object {
        private val logger = LoggerFactory.getLogger(Bot::class.java)
    }

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    private lateinit var room: Room

    private val disposables = CompositeDisposable()

    private val outboundMessageQueue = LinkedList<OutboundMessage>()

    private val recentAccessRequestees = mutableSetOf<User>()

    fun observeLife(): Observable<Boolean> {
        return aliveSubject.hide()
    }

    fun boot(client: StackExchangeClient, roomId: Int) {
        aliveSubject.onNext(true)
        joinRoom(client, roomId)
    }

    private fun die() {
        snoozeUntilAllMessagesAreSent()

        disposables.clear()

        aliveSubject.onNext(false)
        aliveSubject.onComplete()
    }

    private fun joinRoom(client: StackExchangeClient, roomId: Int) {
        room = client.joinRoom(ChatHost.STACK_OVERFLOW, roomId)

        val buildConfig = getBuildConfigUseCase.execute(Unit)
        acceptMessage(messageFormatter.asStatusString(buildConfig))
    }

    fun start() {
        room.accessLevelChangedEventListener = {
            launch {
                if (it.accessLevel == AccessLevel.REQUEST) {
                    recentAccessRequestees += it.targetUser
                }

                accessLevelChangedEventHandler.handle(it, this@Bot)
            }
        }

        room.messagePostedEventListener = {
            launch {
                logger.debug("${it.userName}: ${it.message.content?.sanitize()?.truncate(80)}")
                messageEventHandler.handle(it, this@Bot)
            }
        }

        room.messageEditedEventListener = {
            launch {
                messageEventHandler.handle(it, this@Bot)
            }
        }

        monitorReminders()
        monitorOutboundMessageQueue()
    }

    private fun monitorReminders() = disposables.add(reminderMonitor.start(this))

    private fun monitorOutboundMessageQueue() {
        val disposable = Observable.interval(4000, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe {
                    if (outboundMessageQueue.isNotEmpty()) {
                        val outboundMessage = outboundMessageQueue.poll()
                        sendOutboundMessage(outboundMessage)
                    }
                }

        disposables.add(disposable)
    }

    private fun sendOutboundMessage(outboundMessage: OutboundMessage) {
        logger.debug("sending message: $outboundMessage (${outboundMessageQueue.size} left in queue)")

        if (outboundMessage.targetMessageId == null) {
            room.send(outboundMessage.message)
        } else {
            room.replyTo(outboundMessage.targetMessageId, outboundMessage.message)
        }
    }

    override fun acceptMessage(message: String) {
        outboundMessageQueue.add(OutboundMessage(message))
        logger.debug("queued message: $message")
    }

    override fun acceptReply(message: String, targetMessageId: Long) {
        outboundMessageQueue.add(OutboundMessage(message, targetMessageId))
        logger.debug("queued reply: $message")
    }

    override fun acceptAccessChangeForUserByName(username: String, accessLevel: AccessLevel) {
        val user = recentAccessRequestees.firstOrNull { it.name == username }
                ?: throw IllegalStateException("Cannot find requestee named $username")

        val userAccess = when (accessLevel) {
            AccessLevel.DEFAULT -> "remove"
            AccessLevel.READ -> "read-only"
            AccessLevel.READ_WRITE -> "read-write"
            else -> throw IllegalArgumentException("Cannot set user access to $accessLevel")
        }
        room.setUserAccess(user.id, userAccess)
        logger.info("set access for '$username to $userAccess")
    }

    override fun leaveRoom() {
        die()
    }

    private fun snoozeUntilAllMessagesAreSent() {
        while (outboundMessageQueue.isNotEmpty()) {
            Thread.sleep(1000)
        }
    }

    data class OutboundMessage(
            val message: String,
            val targetMessageId: Long? = null
    )

}

interface Actor {
    fun acceptMessage(message: String)
    fun acceptReply(message: String, targetMessageId: Long)
    fun acceptAccessChangeForUserByName(username: String, accessLevel: AccessLevel)
    fun leaveRoom()
}
