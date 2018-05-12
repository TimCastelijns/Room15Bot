package data.commands

import data.repositories.UserStatsRepository
import fr.tunaki.stackoverflow.chat.User
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class GetUserStatsCommand(
        private val userStatsRepository: UserStatsRepository
) {

    fun execute(user: User): Single<String> =
            Single.zip(userStatsRepository.getNumberOfQuestions(user.id), userStatsRepository.getNumberOfAnswers(user.id),
                    BiFunction<Int, Int, Pair<Int, Int>> { q, a -> Pair(q, a) }
            ).map {
                val ratio = "4:%.1f".format(it.second / (it.first / 4.0))

                "User joined: ${user.name}. Stats are: " +
                        "**Rep:** ${user.reputation} - " +
                        "**Questions:** ${it.first} - " +
                        "**Answers:** ${it.second} ($ratio)"
            }

}
