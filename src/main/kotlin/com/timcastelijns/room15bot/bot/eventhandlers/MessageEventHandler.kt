package com.timcastelijns.room15bot.bot.eventhandlers

import com.timcastelijns.chatexchange.chat.*
import com.timcastelijns.room15bot.bot.Actor
import com.timcastelijns.room15bot.bot.usecases.*
import com.timcastelijns.room15bot.data.BuildConfig
import com.timcastelijns.room15bot.data.repositories.UserRepository
import com.timcastelijns.room15bot.util.Command
import com.timcastelijns.room15bot.util.CommandParser
import com.timcastelijns.room15bot.util.CommandType
import com.timcastelijns.room15bot.util.MessageFormatter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.system.measureTimeMillis

class MessageEventHandler(
        private val getBuildConfigUseCase: GetBuildConfigUseCase,
        private val getUserStatsUseCase: GetUserStatsUseCase,
        private val getStarsDataUseCase: GetStarsDataUseCase,
        private val acceptUserUseCase: AcceptUserUseCase,
        private val rejectUserUseCase: RejectUserUseCase,
        private val syncStarsDataUseCase: SyncStarsDataUseCase,
        private val setReminderUseCase: SetReminderUseCase,
        private val getProfileUseCase: GetProfileUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
        private val updateUseCase: UpdateUseCase,
        private val adamUseCase: AdamUseCase,
        private val maukerUseCase: MaukerUseCase,
        private val ahmadUseCase: AhmadUseCase,
        private val daveUseCase: DaveUseCase,
        private val userRepository: UserRepository,
        private val messageFormatter: MessageFormatter
) {

    companion object {
        private val logger = LoggerFactory.getLogger(MessageEventHandler::class.java)
        private val requesteeUnableToUseChatRegex = Regex("(.+) requested access\\. (?:<b>|\\*\\*)Rep:(?:</b>|\\*\\*) ([1-9]|1[0-9]) (?:.+)")
    }

    private lateinit var actor: Actor

    suspend fun handle(event: MessagePostedEvent, _actor: Actor) {
        actor = _actor
        handleMessage(event.message)
    }

    suspend fun handle(event: MessageEditedEvent, _actor: Actor) {
        actor = _actor
        handleMessage(event.message)
    }

    private suspend fun handleMessage(message: Message)= coroutineScope {
        if (message.isCommand()) {
            val time = measureTimeMillis {
                launch { processCommandMessage(message) }.join()
            }
            logger.debug("processing ${message.content} took $time ms")
        }

        processMessage(message)
    }

    private fun processMessage(message: Message) {
        if (message.content?.contains("dQw4w9WgXcQ") == true) {
            actor.acceptReply(messageFormatter.asRickRollAlertString(), message.id)
        } else if (message.postedByMe) {
            val matcher = requesteeUnableToUseChatRegex.toPattern().matcher(message.plainContent)
            if (matcher.find()) {
                val username = matcher.group(1)
                rejectUserByName(username)
            }
        }
    }

    private suspend fun processCommandMessage(message: Message) {
        val rawCommand = message.content!!
        logger.info("processing command: $rawCommand")

        lateinit var command: Command
        try {
            val time = measureTimeMillis {
                command = CommandParser().parse(rawCommand)
            }
            logger.debug("parsing took $time ms")

        } catch (e: IllegalArgumentException) {
            processUnknownCommand(rawCommand)
            return
        }

        when (command.type) {
            CommandType.HELP -> processHelpCommand(message.id)
            CommandType.STATUS -> processStatusCommand(message.id)
            CommandType.STATS_ME -> {
                val userId = message.user!!.id
                val user = userRepository.getUser(userId)!!
                processShowStatsCommand(user)
            }
            CommandType.STATS_USER -> {
                val userId = command.args!!.toLong()
                val user = userRepository.getUser(userId)!!
                processShowStatsCommand(user)
            }
            CommandType.STARS_ANY -> processShowStarsCommand(null)
            CommandType.STARS_USER -> {
                val username = command.args!!
                processShowStarsCommand(username)
            }
            CommandType.REMIND_ME -> {
                processRemindMeCommand(message.id, command.args!!)
            }
            CommandType.PROFILE -> processProfileCommand(message.id, message.user!!)
            CommandType.UPDATE_PROFILE -> processUpdateProfileCommand(message.id, message.user!!, command.args!!)
            CommandType.ADAM -> processAdamCommand()
            CommandType.MAUKER -> processMaukerCommand()
            CommandType.AHMAD -> processAhmadCommand()
            CommandType.BENZ -> processBenzCommand(message.id, message.user!!)
            CommandType.DAVE -> processDaveCommand()

            CommandType.ACCEPT -> processAcceptCommand(message.id, message.user!!, command.args)
            CommandType.REJECT -> processRejectCommand(message.id, message.user!!, command.args)
            CommandType.LEAVE -> processLeaveCommand(message.user!!)
            CommandType.SYNC_STARS -> processSyncStarsCommand(message.user!!)
            CommandType.UPDATE -> processUpdateCommand(message.user!!, message.id)
        }
    }

    private fun processHelpCommand(messageId: Long) {
        actor.acceptReply(messageFormatter.asHelpString(), messageId)
    }

    private fun processStatusCommand(messageId: Long) {
        val buildConfig = getBuildConfigUseCase.execute(Unit)
        val uptimeHours = getUptimeHours(buildConfig)

        actor.acceptReply(messageFormatter.asStatusString(buildConfig, uptimeHours), messageId)
    }

    private fun getUptimeHours(buildConfig: BuildConfig): Long {
        val buildTime = buildConfig.buildTime

        val ldt = LocalDateTime.parse(buildTime, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        return ChronoUnit.HOURS.between(ldt, LocalDateTime.now(ZoneOffset.UTC))
    }

    private fun processProfileCommand(messageId: Long, user: User) {
        val message = try {
            val profile = getProfileUseCase.execute(user.id)
            messageFormatter.asUserProfile(profile)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptReply(message, messageId)
    }

    private fun processUpdateProfileCommand(messageId: Long, user: User, commandArgs: String) {
        val message = try {
            val params = UpdateProfileCommandParams(user.id, commandArgs)
            updateProfileUseCase.execute(params)
            messageFormatter.asUserProfileUpdated()
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptReply(message, messageId)
    }

    private fun processAcceptCommand(messageId: Long, user: User, username: String?) {
        if (!user.isRoomOwner) {
            actor.acceptReply(messageFormatter.asNoAccessString(), messageId)
            return
        }

        if (username != null) {
            acceptUserByName(username)
        } else {
            actor.provideLatestAccessRequestee()?.let { requestee ->
                acceptUserByName(requestee.name)
            } ?: actor.acceptMessage(messageFormatter.asRequesteeNotFound())
        }
    }

    private fun acceptUserByName(username: String) {
        val message = acceptUserUseCase.execute(username)
        actor.acceptMessage(message)

        setUserAccess(username, AccessLevel.READ_WRITE)
    }

    private fun processRejectCommand(messageId: Long, user: User, username: String?) {
        if (!user.isRoomOwner) {
            actor.acceptReply(messageFormatter.asNoAccessString(), messageId)
            return
        }

        if (username != null) {
            rejectUserByName(username)
        } else {
            actor.provideLatestAccessRequestee()?.let { requestee ->
                rejectUserByName(requestee.name)
            } ?: actor.acceptMessage(messageFormatter.asRequesteeNotFound())
        }
    }

    private fun rejectUserByName(username: String) {
        val rejectMessage = rejectUserUseCase.execute(username)
        actor.acceptMessage(rejectMessage)

        setUserAccess(username, AccessLevel.DEFAULT)
    }

    private fun setUserAccess(username: String, accessLevel: AccessLevel) {
        try {
            actor.acceptAccessChangeForUserByName(username, accessLevel)
        } catch (e: IllegalStateException) {
            actor.acceptMessage("Illegal state: ${e.message}")
        } catch (e: IllegalArgumentException) {
            actor.acceptMessage("Illegal argument passed: ${e.message}")
        }
    }

    private fun processLeaveCommand(user: User) {
        if (user.id == 1843331L) {
            actor.acceptMessage(messageFormatter.asLeavingString())
            actor.leaveRoom()
        } else {
            actor.acceptMessage(messageFormatter.asNoAccessString())
        }
    }

    private suspend fun processShowStatsCommand(user: User) = launch {
        val stats = getUserStatsUseCase.execute(user)
        actor.acceptMessage(messageFormatter.asStatsString(user, stats))
    }

    private suspend fun processSyncStarsCommand(user: User) {
        if (user.id != 1843331L) {
            messageFormatter.asNoAccessString()
            return
        }

        actor.acceptMessage(messageFormatter.asStartingJobString())

        val measuredTime = measureTimeMillis {
            syncStarsDataUseCase.execute(Unit)
        }

        actor.acceptMessage(messageFormatter.asDoneString(measuredTime))
    }

    private fun processShowStarsCommand(username: String?) {
        val data = getStarsDataUseCase.execute(username)
        actor.acceptMessage(messageFormatter.asTableString(data))
    }

    private fun processRemindMeCommand(messageId: Long, commandArgs: String) {
        val params = SetReminderCommandParams(messageId, commandArgs)

        val message = try {
            val data = setReminderUseCase.execute(params)
            messageFormatter.asReminderString(data)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptMessage(message)
    }

    private fun processAdamCommand() {
        val message = try {
            val data = adamUseCase.execute(Unit)
            messageFormatter.asAdamString(data)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptMessage(message)
    }

    private fun processMaukerCommand() {
        val message = try {
            val data = maukerUseCase.execute(Unit)
            messageFormatter.asMaukerString(data)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptMessage(message)
    }

    private fun processAhmadCommand() {
        val message = try {
            val data = ahmadUseCase.execute(Unit)
            messageFormatter.asAhmadString(data)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptMessage(message)
    }

    private fun processBenzCommand(messageId: Long, user: User) {
        if (user.id == 4467208L) {
            actor.acceptReply(messageFormatter.asBenzString(), messageId)
        } else {
            actor.acceptReply(messageFormatter.asBenzPeasantString(), messageId)
        }
    }

    private fun processDaveCommand() {
        val message = try {
            val data = daveUseCase.execute(Unit)
            messageFormatter.asDaveString(data)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptMessage(message)
    }

    private fun processUpdateCommand(user: User, messageId: Long) {
        if (user.id != 1843331L) {
            messageFormatter.asNoAccessString()
            return
        }

        actor.acceptReply(messageFormatter.asBeRightBackString(), messageId)

        try {
            updateUseCase.execute(Unit)
        } catch (e: IOException) {
            logger.error(e.message)
            actor.acceptMessage(messageFormatter.asUpdateErrorString())
        }
    }

    private fun processUnknownCommand(rawCommand: String) =
            actor.acceptMessage(messageFormatter.asUnknownCommandString(rawCommand))

}

private fun Message.isCommand() =
        content?.run {
            startsWith("!") && length > 1
        } ?: false

private val Message.postedByMe
    get() = user?.id == 9676629L