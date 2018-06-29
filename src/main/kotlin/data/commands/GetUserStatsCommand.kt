package data.commands

import data.repositories.UserStatsRepository
import fr.tunaki.stackoverflow.chat.User
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import util.UserNameValidator

class GetUserStatsCommand(
        private val userStatsRepository: UserStatsRepository,
        private val userNameValidator: UserNameValidator
) {

    fun execute(user: User): Single<String> =
            Single.zip(userStatsRepository.getNumberOfQuestions(user.id), userStatsRepository.getNumberOfAnswers(user.id),
                    BiFunction<Int, Int, Pair<Int, Int>> { q, a -> Pair(q, a) }
            ).map {
                val ratio = "4:%.1f".format(it.second / (it.first / 4.0))
                val isNameValid = userNameValidator.isValid(user.name)

                "${user.name} joined. " +
                        "**Rep:** ${user.reputation} - " +
                        "**Questions:** ${it.first} - " +
                        "**Answers:** ${it.second} ($ratio) " +
                        "**Name**: ${if (isNameValid) "✓" else "x"}"
            }

}
