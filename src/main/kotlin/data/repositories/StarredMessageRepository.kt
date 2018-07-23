package data.repositories

import com.timcastelijns.chatexchange.chat.ChatHost
import io.reactivex.Single
import network.StarService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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

                        starredMessages += StarredMessage(username, "", stars, "http")
                    }

                    starredMessages
                }
    }

    fun getAllStarredMessages(): Single<List<StarredMessage>> {
        return starService.getStarsDataByPage(page = 1)
                .map { Jsoup.parse(it) }
                .map { document -> document.extractStarredMessages() }
    }

}

data class StarredMessage(
        val username: String,
        val message: String,
        val stars: Int,
        val permanentLink: String
)

private fun Document.extractStarredMessages(): List<StarredMessage> {
    val starredMessages = mutableListOf<StarredMessage>()
    val elements = select("div.monologue")

    elements.forEach {
        val username = it.getElementsByAttribute("title").first().text()
        val message = with(it.select("div.message").first().select("div.content").first()) {
            if (hasText()) {
                text()
            } else {
                "-image-"
            }
        }

        val times = it.getElementsByClass("times").first().text()
        val stars = if (times.isNotEmpty()) times.toInt() else 1

        val href = it.select("div.message").first().select("a[href^=\"/transcript\"]").attr("href")
        val permalink = "${ChatHost.STACK_OVERFLOW.baseUrl}$href"

        starredMessages += StarredMessage(username, message, stars, permalink)
    }

    return starredMessages
}
