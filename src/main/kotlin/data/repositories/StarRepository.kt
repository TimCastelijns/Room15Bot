package data.repositories

import io.reactivex.Single
import network.StarService
import org.jsoup.Jsoup

class StarRepository (
        private val starService: StarService
) {

    fun getStarData(): Single<List<String>> {
        return starService.getStars()
                .map {
                    val document = Jsoup.parse(it)
                    val elements = document.select("div.monologue")
                    elements.map {
                        it.classNames().last()
                    }
                }
    }

}
