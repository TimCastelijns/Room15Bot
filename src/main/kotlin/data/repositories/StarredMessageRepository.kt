package data.repositories

import io.reactivex.Single
import network.StarService
import org.jsoup.Jsoup

class StarredMessageRepository(
        private val starService: StarService
) {

    fun getStarredMessages(): Single<List<StarredMessage>> {
        return starService.getStarsData()
                .map { Jsoup.parse(it) }
                .map { document ->
                    val starredMessages = mutableListOf<StarredMessage>()
                    val elements = document.select("div.monologue").subList(0, 5)

                    elements.forEach {
                        val username = it.getElementsByAttribute("title").first().text()

                        val times = it.getElementsByClass("times").first().text()
                        val stars = if (times.isNotEmpty()) times.toInt() else 1

                        starredMessages += StarredMessage(username, stars, "http")
                    }

                    starredMessages
                }
    }

}

data class StarredMessage(
        val username: String,
        val stars: Int,
        val permanentLink: String
)
