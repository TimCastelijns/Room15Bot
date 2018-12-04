package com.timcastelijns.room15bot.data.repositories

import com.timcastelijns.room15bot.network.UserStatsService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class UserStatsRepository(
        private val userStatsService: UserStatsService
) {

    suspend fun getNumberOfQuestions(userId: Long): Int {
        val data = userStatsService.getUserProfileQuestions(userId).await()

        return Jsoup.parse(data).valueOfFirstCountClass()
    }

    suspend fun getNumberOfAnswers(userId: Long): Int {
        val data = userStatsService.getUserProfileAnswers(userId).await()

        return Jsoup.parse(data).valueOfFirstCountClass()
    }

    private fun Document.valueOfFirstCountClass(): Int {
        val text = getElementsByClass("count").first().text()
        return if (text.isNotEmpty()) text.replace("," , "").toInt() else 0
    }

}
