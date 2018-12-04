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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class Bot(
        private val accessLevelChangedEventHandler: AccessLevelChangedEventHandler,
        private val messageEventHandler: MessageEventHandler,
        private val reminderMonitor: ReminderMonitor,
        private val getBuildConfigUseCase: GetBuildConfigUseCase,
        private val messageFormatter: MessageFormatter
) : CoroutineScope, Actor {

    companion object {
        private val logger = LoggerFactory.getLogger(Bot::class.java)
        private const val RESPOND_TO_ACCEPTANCE_DEADLINE = 1_800_000L
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    private lateinit var room: Room

    private val disposables = CompositeDisposable()

    private val outboundMessageQueue = LinkedList<OutboundMessage>()

    private val recentAccessRequestees = mutableSetOf<User>()
    private val recentAccessGrants = mutableSetOf<Pair<User, Instant>>()

    fun observeLife(): Observable<Boolean> {
        return aliveSubject.hide()
    }

    fun boot(client: StackExchangeClient, roomId: Int) {
        aliveSubject.onNext(true)
        joinRoom(client, roomId)
    }

    private fun die() {
        snoozeUntilAllMessagesAreSent()

        job.cancel()
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

        room.messagePostedEventListener = { messagePostedEvent ->
            launch {
                logger.debug("${messagePostedEvent.userName}: ${messagePostedEvent.message.content?.sanitize()?.truncate(80)}")
                messageEventHandler.handle(messagePostedEvent, this@Bot)

                // If this message was posted by a user who was recently granted write access,
                // remove him from the set so he is no longer monitored.
                recentAccessGrants.removeIf { it.first.id == messagePostedEvent.userId }
            }
        }

        room.messageEditedEventListener = {
            launch {
                messageEventHandler.handle(it, this@Bot)
            }
        }

        monitorReminders()
        monitorRecentAccessGrants()
        monitorOutboundMessageQueue()
    }

    private fun monitorReminders() = disposables.add(reminderMonitor.start(this))

    private fun monitorRecentAccessGrants() {
        val disposable = Observable.interval(5, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribe {
                    recentAccessGrants.forEach { accessGrant ->
                        if (accessGrant.respondDeadlineExceeded) {
                            acceptAccessChangeForUserByName(accessGrant.first.name, AccessLevel.DEFAULT)

                            // This user is processed. Remove them from the collection.
                            recentAccessGrants.remove(accessGrant)
                            acceptMessage(messageFormatter.asRespondAcceptanceDeadlineExceeded(accessGrant.first))
                        }
                    }
                }

        disposables.add(disposable)
    }

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

    override fun provideLatestAccessRequestee() = recentAccessRequestees.lastOrNull()

    override fun acceptMessage(message: String) {
        outboundMessageQueue.add(OutboundMessage(message))
        logger.debug("queued message: $message")
    }

    override fun acceptReply(message: String, targetMessageId: Long) {
        outboundMessageQueue.add(OutboundMessage(message, targetMessageId))
        logger.debug("queued reply: $message")
    }

    override fun acceptAccessChangeForUserByName(username: String, accessLevel: AccessLevel) {
        val user = recentAccessRequestees.firstOrNull { it.name.equals(username, ignoreCase = true) }
                ?: throw IllegalStateException("Cannot find requestee named $username")

        val userAccess = when (accessLevel) {
            AccessLevel.DEFAULT -> "remove"
            AccessLevel.READ -> "read-only"
            AccessLevel.READ_WRITE -> "read-write"
            else -> throw IllegalArgumentException("Cannot set user access to $accessLevel")
        }
        room.setUserAccess(user.id, userAccess)
        logger.info("set access for '$username to $userAccess")

        if (accessLevel == AccessLevel.READ_WRITE) {
            // Add users who have just been granted write access to the collection
            // so we can monitor if they respond in a timely fashion.
            recentAccessGrants.add(Pair(user, Instant.now()))
        }

        // This user is processed. Remove them from the collection.
        recentAccessRequestees.remove(user)
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

    private val Pair<User, Instant>.respondDeadlineExceeded
        get() = Instant.now().isAfter(second.plusMillis(RESPOND_TO_ACCEPTANCE_DEADLINE))

}

interface Actor {
    fun provideLatestAccessRequestee(): User?
    fun acceptMessage(message: String)
    fun acceptReply(message: String, targetMessageId: Long)
    fun acceptAccessChangeForUserByName(username: String, accessLevel: AccessLevel)
    fun leaveRoom()
}
