package com.timcastelijns.room15bot.data.repositories

import com.timcastelijns.chatexchange.chat.ChatHost
import com.timcastelijns.room15bot.data.StarredMessage
import com.timcastelijns.room15bot.network.StarService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

// Example: Fri 8:07 PM
private val FORMATTER_THIS_WEEK = DateTimeFormatter.ofPattern("E h:mm a")

// Example: Jan 4 9:01 PM
private val FORMATTER_THIS_YEAR = DateTimeFormatterBuilder()
        .appendPattern("MMM d h:mm a")
        .parseDefaulting(ChronoField.YEAR, LocalDate.now(ZoneOffset.UTC).year.toLong())
        .toFormatter()

// Example: Dec 18 '18 3:37 PM
private val FORMATTER_PREVIOUS_YEAR = DateTimeFormatter.ofPattern("MMM d ''yy h:mm a")

class StarredMessageRepository(
        private val starService: StarService
) {

    suspend fun getStarredMessagesByPage(page: Int): List<StarredMessage> {
        val data = starService.getStarsDataByPage(page).await()
        return Jsoup.parse(data).extractStarredMessages()
    }

    suspend fun getNumberOfStarredMessagesPages(): Int {
        val data = starService.getStarsDataByPage(1).await()
        return Jsoup.parse(data)
                .select("div.pager").first()
                .select("a[href^=\"?tab=stars&page=\"]")
                .secondToLast()?.run {
                    getElementsByClass("page-numbers").first().text().toInt()
                } ?: 0
    }
}

private fun Document.extractStarredMessages(): List<StarredMessage> {
    val starredMessages = mutableListOf<StarredMessage>()
    val elements = select("div.monologue")

    val now = LocalDateTime.now(ZoneOffset.UTC)
    elements.forEach {
        val message = with(it.select("div.message").first().select("div.content").first()) {
            if (hasText()) {
                text()
            } else {
                "-image-"
            }
        }
        val username = try {
            it.getElementsByAttribute("title").first().text()
        } catch (e: Exception) {
            // Deleted users do not have a a[title] because their is no profile to link to. Their name is in div.username
            it.getElementsByClass("username").first().text()
        }

        val times = it.getElementsByClass("times").first().text()
        val stars = if (times.isNotEmpty()) times.toInt() else 1

        val href = it.select("div.message").first().select("a[href^=\"/transcript\"]").attr("href")
        val permalink = "${ChatHost.STACK_OVERFLOW.baseUrl}$href"

        val timestamp = it.getElementsByClass("timestamp").first().text()

        val ageInDays = when {
            timestamp[0].isDigit() -> // Today
                0
            timestamp.startsWith("yst") -> // Yesterday
                1
            "'" in timestamp -> // A previous year
                ChronoUnit.DAYS.between(LocalDate.parse(timestamp, FORMATTER_PREVIOUS_YEAR), now).toInt()
            timestamp.length > 12 ->  // This year
                ChronoUnit.DAYS.between(LocalDate.parse(timestamp, FORMATTER_THIS_YEAR), now).toInt()
            else -> {// This week
                val then = DayOfWeek.from(FORMATTER_THIS_WEEK.parse(timestamp)).value
                val today = now.dayOfWeek.value
                daysAgo(today, then)
            }
        }

        starredMessages += StarredMessage(username, message, stars, permalink, ageInDays)
    }

    return starredMessages
}

private fun <T> List<T>.secondToLast(): T? = get(size - 2)

/**
 * Tells how many days ago [then] was compared to [now], given they represent 1-indexed days of the week
 * and [then] is at most 6 days ago.
 */
private fun daysAgo(now: Int, then: Int): Int = if (now > then) now - then else 7 - Math.abs(then - now)
