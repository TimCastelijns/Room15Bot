package data.commands

import com.timcastelijns.chatexchange.chat.User
import data.repositories.UserStatsRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class GetUserStatsCommand(
        private val userStatsRepository: UserStatsRepository
) : SingleCommand<User, String> {

    override fun execute(params: User): Single<String> = Single.zip(userStatsRepository.getNumberOfQuestions(params.id), userStatsRepository.getNumberOfAnswers(params.id),
            BiFunction<Int, Int, Pair<Int, Int>> { q, a -> Pair(q, a) })
            .map {
                val answersPerQuestion = answersPerQuestion(it.first, it.second)

                "**Rep:** ${params.reputation} - " +
                        "**Questions:** ${it.first} - " +
                        "**Answers:** ${it.second} (ratio ${ratio(answersPerQuestion)})"
            }
            .subscribeOn(Schedulers.io())

    private fun answersPerQuestion(questions: Int, answers: Int) = when (answers) {
        0 -> 0F
        else -> (answers / (questions / 4.0)).toFloat()
    }

    private fun ratio(answersPerQuestion: Float) = "4:%.1f".format(answersPerQuestion)

}
