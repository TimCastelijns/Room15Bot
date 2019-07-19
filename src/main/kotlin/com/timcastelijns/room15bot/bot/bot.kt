package com.timcastelijns.room15bot.bot

import com.timcastelijns.chatexchange.chat.*
import com.timcastelijns.room15bot.bot.eventhandlers.AccessLevelChangedEventHandler
import com.timcastelijns.room15bot.bot.eventhandlers.MessageEventHandler
import com.timcastelijns.room15bot.bot.monitors.ReminderMonitor
import com.timcastelijns.room15bot.bot.usecases.GetBuildConfigUseCase
import com.timcastelijns.room15bot.bot.usecases.GetTopMessageUseCase
import com.timcastelijns.room15bot.bot.usecases.SyncStarsDataUseCase
import com.timcastelijns.room15bot.bot.usecases.truncate
import com.timcastelijns.room15bot.data.db.UserDao
import com.timcastelijns.room15bot.util.MessageFormatter
import com.timcastelijns.room15bot.util.sanitize
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

class Bot(
        private val accessLevelChangedEventHandler: AccessLevelChangedEventHandler,
        private val messageEventHandler: MessageEventHandler,
        private val reminderMonitor: ReminderMonitor,
        private val getBuildConfigUseCase: GetBuildConfigUseCase,
        private val syncStarsDataUseCase: SyncStarsDataUseCase,
        private val getTopMessageUseCase: GetTopMessageUseCase,
        private val messageFormatter: MessageFormatter,
        private val userDao: UserDao
) : CoroutineScope, Actor {

    companion object {
        private val logger = LoggerFactory.getLogger(Bot::class.java)
        private const val TIME_USER_HAS_TO_ACK_RULES_MS = 1_800_000L // 30 mins
        private const val TIME_OWNER_HAS_TO_ACCEPT_REQUEST_MS = 2_100_000L // 35 mins
    }

    private val job = kotlinx.coroutines.Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    private lateinit var room: Room

    private val disposables = CompositeDisposable()

    private val outboundMessageQueue = LinkedList<OutboundMessage>()

    private val recentAccessRequests = mutableSetOf<AccessRequest>()
    private val recentAccessGrants = mutableSetOf<AccessGrant>()

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
                    recentAccessRequests += AccessRequest(it.targetUser, Instant.now())
                }

                accessLevelChangedEventHandler.handle(it, this@Bot)
            }
        }

        room.messagePostedEventListener = { messagePostedEvent ->
            launch {
                logger.debug("${messagePostedEvent.userId} - ${messagePostedEvent.userName}")
                userDao.create(messagePostedEvent.userId, messagePostedEvent.userName)
            }

            launch {
                logger.debug("${messagePostedEvent.userName}: ${messagePostedEvent.message.content?.sanitize()?.truncate(80)}")
                messageEventHandler.handle(messagePostedEvent, this@Bot)

                // If this message was posted by a user who was recently granted write access,
                // make it so he is no longer monitored.
                recentAccessGrants.firstOrNull { it.user.id == messagePostedEvent.userId }?.shouldMonitor = false
            }
        }

        room.messageEditedEventListener = {
            launch {
                messageEventHandler.handle(it, this@Bot)
            }
        }

        monitorReminders()
        monitorAccessGrants()
        monitorOutboundMessageQueue()
        monitorAutoStarsDataSync()
        monitorDailyTopMessage()
    }

    private fun monitorReminders() = disposables.add(reminderMonitor.start(this))

    private fun monitorAccessGrants() {
        val disposable = Observable.interval(5, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribe {
                    logger.debug("Checking access grants, have ${recentAccessGrants.size}")
                    recentAccessGrants.filter { accessGrant -> accessGrant.shouldMonitor }
                            .forEach { accessGrant ->
                                if (accessGrant.respondDeadlineExceeded) {
                                    logger.debug("deadline exceeded for ${accessGrant.user.name}. Granted at ${accessGrant.timestamp}")
                                    acceptAccessChangeForUserByName(accessGrant.user.name, AccessLevel.DEFAULT)

                                    // Access is removed, this data is no longer needed.
                                    recentAccessGrants.remove(accessGrant)
                                    acceptMessage(messageFormatter.asRespondAcceptanceDeadlineExceeded(accessGrant.user))
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

    private fun monitorAutoStarsDataSync() {
        val disposable = Observable.interval(1, TimeUnit.HOURS)
                .filter {
                    val now = LocalDateTime.now(ZoneOffset.UTC)

                    // Mondays somewhere between 09.00 and 10.00 UTC.
                    now.dayOfWeek == DayOfWeek.MONDAY && now.hour == 9
                }
                .observeOn(Schedulers.io())
                .subscribe {
                    launch {
                        val measuredTime = measureTimeMillis {
                            syncStarsDataUseCase.execute(Unit)
                        }

                        acceptMessage(messageFormatter.asJobDoneString(
                                Job.STARS_DATA_SYNC, measuredTime))
                    }
                }

        disposables.add(disposable)
    }

    private fun monitorDailyTopMessage() {
        val disposable = Observable.interval(1, TimeUnit.HOURS)
                .filter {
                    val now = LocalDateTime.now(ZoneOffset.UTC)

                    // Daily somewhere between 10.00 and 11.00 UTC.
                    now.hour == 10
                }
                .observeOn(Schedulers.io())
                .subscribe {
                    launch {
                        logger.debug("Checking what the top message last year was")
                        val message = try {
                            val data = getTopMessageUseCase.execute(365) // What are leap years?
                            messageFormatter.asTopMessageString(data)
                        } catch (e: IllegalArgumentException) {
                            e.message!!
                        }
                        acceptMessage(messageFormatter.asTopMessageAnnouncementString())
                        acceptMessage(message)
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

    override fun provideLatestAccessRequestee() = recentAccessRequests.filterNot { it.processed }.filterNot { it.acceptDeadlineExceeded }.lastOrNull()?.user

    override fun acceptMessage(message: String) {
        outboundMessageQueue.add(OutboundMessage(message))
        logger.debug("queued message: $message")
    }

    override fun acceptReply(message: String, targetMessageId: Long) {
        outboundMessageQueue.add(OutboundMessage(message, targetMessageId))
        logger.debug("queued reply: $message")
    }

    override fun acceptAccessChangeForUserByName(username: String, accessLevel: AccessLevel) {
        val user = recentAccessRequests.firstOrNull { it.user.name.equals(username, ignoreCase = true) }?.user
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
            recentAccessGrants.add(AccessGrant(user, Instant.now()))
        }

        // This user is processed.
        recentAccessRequests.firstOrNull { it.user.name.equals(username, ignoreCase = true) }?.processed = true
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

    private val AccessGrant.respondDeadlineExceeded
        get() = Instant.now().isAfter(timestamp.plusMillis(TIME_USER_HAS_TO_ACK_RULES_MS))

    private data class AccessRequest(val user: User, val timestamp: Instant, var processed: Boolean = false)

    private val AccessRequest.acceptDeadlineExceeded
        get() = Instant.now().isAfter(timestamp.plusMillis(TIME_OWNER_HAS_TO_ACCEPT_REQUEST_MS))

    private data class AccessGrant(val user: User, val timestamp: Instant, var shouldMonitor: Boolean = true)

}

interface Actor {
    fun provideLatestAccessRequestee(): User?
    fun acceptMessage(message: String)
    fun acceptReply(message: String, targetMessageId: Long)
    fun acceptAccessChangeForUserByName(username: String, accessLevel: AccessLevel)
    fun leaveRoom()
}
