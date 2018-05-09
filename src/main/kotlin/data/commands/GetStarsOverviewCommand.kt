package data.commands

import data.repositories.StarredMessage
import data.repositories.StarredMessageRepository
import io.reactivex.Single

class GetStarsOverviewCommand (
        private val starredMessageRepository: StarredMessageRepository
) {
    fun execute(): Single<String> {
        return starredMessageRepository.getStarredMessages()
                .map { it.asTableString() }
    }

    private fun List<StarredMessage>.asTableString(): String {
        var message = "    Username".padEnd(24) + "| Stars | Link\n" +
                "    ----------------------------------\n"

        forEach {
            var stars = it.stars.toString()
            if (stars.length == 1) stars = " $stars"
            message += "    ${it.username.padEnd(20)}|   $stars  | ${it.permanentLink}\n"
        }

        return message
    }

}
