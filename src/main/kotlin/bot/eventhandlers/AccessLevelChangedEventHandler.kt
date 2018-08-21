package bot.eventhandlers

import bot.Actor
import bot.usecases.GetUserStatsUseCase
import com.timcastelijns.chatexchange.chat.AccessLevel
import com.timcastelijns.chatexchange.chat.AccessLevelChangedEvent
import com.timcastelijns.chatexchange.chat.User
import util.MessageFormatter

class AccessLevelChangedEventHandler(
        private val getUserStatsUseCase: GetUserStatsUseCase,
        private val messageFormatter: MessageFormatter
) {

    suspend fun handle(event: AccessLevelChangedEvent, actor: Actor) {
        if (event.accessLevel == AccessLevel.REQUEST) {
            val message = processUserRequestedAccess(event.targetUser)
            return actor.acceptMessage(message)
        }
    }

    private suspend fun processUserRequestedAccess(user: User): String {
        val stats = getUserStatsUseCase.execute(user)
        return messageFormatter.asRequestedAccessString(user, stats)
    }
}
