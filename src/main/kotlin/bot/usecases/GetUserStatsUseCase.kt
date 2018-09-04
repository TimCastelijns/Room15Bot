package bot.usecases

import com.timcastelijns.chatexchange.chat.User
import data.repositories.UserStatsRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class GetUserStatsUseCase(
        private val userStatsRepository: UserStatsRepository
) : SingleUseCase<User, String> {

    override fun execute(params: User): Single<String> = Single.zip(userStatsRepository.getNumberOfQuestions(params.id), userStatsRepository.getNumberOfAnswers(params.id),
            BiFunction<Int, Int, Pair<Int, Int>> { q, a -> Pair(q, a) })
            .map {
                val answersPerQuestion = answersPerQuestion(it.first, it.second)

                "**Rep:** ${params.reputation} - " +
                        "**Questions:** ${it.first} - " +
                        "**Answers:** ${it.second} (ratio ${ratio(answersPerQuestion)})"
            }
            .subscribeOn(Schedulers.io())

    private fun answersPerQuestion(questions: Int, answers: Int) =
        answers / (questions / 4f)

    private fun ratio(answersPerQuestion: Float) = "4:%.1f".format(answersPerQuestion).stripUnnecessaryDecimal()

    private fun String.stripUnnecessaryDecimal() = removeSuffix(".0")

}
