package com.timcastelijns.room15bot.bot.eventhandlers

import com.timcastelijns.chatexchange.chat.*
import com.timcastelijns.room15bot.bot.Actor
import com.timcastelijns.room15bot.bot.usecases.*
import com.timcastelijns.room15bot.data.repositories.UserRepository
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import com.timcastelijns.room15bot.util.Command
import com.timcastelijns.room15bot.util.CommandParser
import com.timcastelijns.room15bot.util.CommandType
import com.timcastelijns.room15bot.util.MessageFormatter
import kotlin.system.measureTimeMillis

class MessageEventHandler(
        private val getBuildConfigUseCase: GetBuildConfigUseCase,
        private val getUserStatsUseCase: GetUserStatsUseCase,
        private val getStarsDataUseCase: GetStarsDataUseCase,
        private val acceptUserUseCase: AcceptUserUseCase,
        private val rejectUserUseCase: RejectUserUseCase,
        private val syncStarsDataUseCase: SyncStarsDataUseCase,
        private val setReminderUseCase: SetReminderUseCase,
        private val cfUseCase: CfUseCase,
        private val userRepository: UserRepository,
        private val messageFormatter: MessageFormatter
) {

    companion object {
        private val logger = LoggerFactory.getLogger(MessageEventHandler::class.java)
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

    private suspend fun handleMessage(message: Message) {
        if (message.containsCommand()) {
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
            CommandType.CF -> processCfCommand(command.args)

            CommandType.ACCEPT -> processAcceptCommand(message.user!!, command.args!!)
            CommandType.REJECT -> processRejectCommand(message.user!!, command.args!!)
            CommandType.LEAVE -> processLeaveCommand(message.user!!)
            CommandType.SYNC_STARS -> processSyncStarsCommand(message.user!!)
        }
    }

    private fun processHelpCommand(messageId: Long) {
        actor.acceptReply(messageFormatter.asHelpString(), messageId)
    }

    private fun processStatusCommand(messageId: Long) {
        val buildConfig = getBuildConfigUseCase.execute(Unit)
        actor.acceptReply(messageFormatter.asStatusString(buildConfig), messageId)
    }

    private fun processAcceptCommand(user: User, username: String) {
        if (!user.isRoomOwner) {
            return
        }

        val message = acceptUserUseCase.execute(username)
        actor.acceptMessage(message)

        setUserAccess(username, AccessLevel.READ_WRITE)
    }

    private fun processRejectCommand(user: User, username: String) {
        if (!user.isRoomOwner) {
            return
        }

        val message = rejectUserUseCase.execute(username)
        actor.acceptMessage(message)

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
            actor.acceptMessage("\uD83D\uDD95\uD83C\uDFFB")
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

    private fun processCfCommand(commandArgs: String?) {
        val message = try {
            val data = cfUseCase.execute(commandArgs)
            messageFormatter.asCfString(data)
        } catch (e: IllegalArgumentException) {
            e.message!!
        }

        actor.acceptMessage(message)
    }

    private fun processUnknownCommand(rawCommand: String) =
            actor.acceptMessage(messageFormatter.asUnknownCommandString(rawCommand))

}

private fun Message.containsCommand() = content?.startsWith("!") ?: false
