package data.commands

import fr.tunaki.stackoverflow.chat.User
import io.reactivex.Single

class GetUserStatsCommand {

    fun execute(user: User): Single<String> {
        return Single.just("User joined: ${user.name}. Stats are:\nRep: ${user.reputation}")
    }

}
