package com.timcastelijns.room15bot.bot.eventhandlers

import com.timcastelijns.room15bot.bot.Actor
import com.timcastelijns.room15bot.bot.usecases.GetUserStatsUseCase
import com.timcastelijns.chatexchange.chat.AccessLevel
import com.timcastelijns.chatexchange.chat.AccessLevelChangedEvent
import com.timcastelijns.chatexchange.chat.User
import com.timcastelijns.room15bot.bot.usecases.CreateAccessRequestParams
import com.timcastelijns.room15bot.bot.usecases.CreateAccessRequestUseCase
import org.slf4j.LoggerFactory
import com.timcastelijns.room15bot.util.MessageFormatter
import kotlin.system.measureTimeMillis

class AccessLevelChangedEventHandler(
        private val getUserStatsUseCase: GetUserStatsUseCase,
        private val createAccessRequestUseCase: CreateAccessRequestUseCase,
        private val messageFormatter: MessageFormatter
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AccessLevelChangedEventHandler::class.java)
    }

    suspend fun handle(event: AccessLevelChangedEvent, actor: Actor) {
        logger.debug("event: ${event.accessLevel}")
        if (event.accessLevel == AccessLevel.REQUEST) {
            lateinit var message: String
            val time = measureTimeMillis {
                message = processUserRequestedAccess(event.targetUser)
            }

            logger.debug("processing $event took $time ms")
            return actor.acceptMessage(message)
        }
    }

    private suspend fun processUserRequestedAccess(user: User): String {
        storeAccessRequest(user)

        val stats = getUserStatsUseCase.execute(user)
        return messageFormatter.asRequestedAccessString(user, stats)
    }

    private fun storeAccessRequest(user: User) {
        createAccessRequestUseCase.execute(CreateAccessRequestParams(user.id, user.name))
    }

}
