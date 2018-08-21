package bot.usecases

import com.timcastelijns.chatexchange.chat.User
import data.repositories.UserStatsRepository

class GetUserStatsUseCase(
        private val userStatsRepository: UserStatsRepository
) : AsyncUseCase<User, UserStats> {

    override suspend fun execute(params: User): UserStats {
        val nrQuestions = userStatsRepository.getNumberOfQuestions(params.id)
        val nrAnswers = userStatsRepository.getNumberOfAnswers(params.id)

        val answersPerQuestion = answersPerQuestion(nrQuestions, nrAnswers)

        return UserStats(params.reputation, nrQuestions, nrAnswers, ratio(answersPerQuestion))
    }

    private fun answersPerQuestion(questions: Int, answers: Int) = when (answers) {
        0 -> 0F
        else -> (answers / (questions / 4.0)).toFloat()
    }

    private fun ratio(answersPerQuestion: Float) = "4:%.1f".format(answersPerQuestion).stripUnnecessaryDecimal()

    private fun String.stripUnnecessaryDecimal() = removeSuffix(".0")

}

data class UserStats(
        val reputation: Int,
        val nrQuestions: Int,
        val nrAnswers: Int,
        val formattedRatio: String
)
