package data.repositories

import io.reactivex.Single
import network.UserStatsService
import org.jsoup.Jsoup

class UserStatsRepository(
        private val userStatsService: UserStatsService
) {

    fun getNumberOfQuestions(userId: Long): Single<Int> =
            userStatsService.getUserProfileQuestions(userId)
                    .map { Jsoup.parse(it) }
                    .map { document ->
                        val count = document.getElementsByClass("count").first().text()
                        val nrQuestions = if (count.isNotEmpty()) count.toInt() else 0

                        nrQuestions
                    }

    fun getNumberOfAnswers(userId: Long): Single<Int> =
            userStatsService.getUserProfileAnswers(userId)
                    .map { Jsoup.parse(it) }
                    .map { document ->
                        val count = document.getElementsByClass("count").first().text()
                        val nrAnswers = if (count.isNotEmpty()) count.toInt() else 0

                        nrAnswers
                    }

}
