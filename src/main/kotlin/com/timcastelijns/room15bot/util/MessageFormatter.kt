package com.timcastelijns.room15bot.util

import com.timcastelijns.chatexchange.chat.User
import com.timcastelijns.room15bot.bot.usecases.StarsData
import com.timcastelijns.room15bot.bot.usecases.UserStats
import com.timcastelijns.room15bot.bot.usecases.truncate
import com.timcastelijns.room15bot.data.BuildConfig
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MessageFormatter {

    fun asHelpString() = "You can find information on what I can do " +
            "[here](https://github.com/TimCastelijns/Room15Bot#usage)"

    fun asStatusString(buildConfig: BuildConfig, uptimeHours: Long = 0) =
            "Online since ${buildConfig.buildTime} (UTC). " +
                    "Running on ${buildConfig.branch}@${buildConfig.commit}. " +
                    "Uptime: ${uptimeHours}h"

    fun asTableString(starsData: StarsData) = with(starsData) {
        if (starredMessages.isEmpty()) {
            return "No starred messages found"
        }

        val nameColumnMinLength = 6
        val nameColumnMaxLength = 10
        val messageColumnMaxLength = 48

        val longestNameLength = starredMessages.maxBy { it.username.length }!!.username.length
        val nameColumnLength = when {
            longestNameLength >= nameColumnMaxLength -> nameColumnMaxLength
            longestNameLength < nameColumnMinLength -> nameColumnMinLength
            else -> longestNameLength
        }

        val userHeader = "User".padEnd(nameColumnLength)
        val messageHeader = "Message ($totalStarredMessages)".padEnd(messageColumnMaxLength)
        val starsHeader = "Stars ($totalStars)"

        val header = " $userHeader | $messageHeader | $starsHeader"
        val separator = "-".repeat(header.length)

        val table = mutableListOf<String>()
        table.add(header)
        table.add(separator)

        starredMessages.forEach {
            val user = it.username.truncate(nameColumnLength).padEnd(nameColumnLength)
            val message = it.message.sanitize().truncate(messageColumnMaxLength).padEnd(messageColumnMaxLength)
            val stars = it.stars.toString().truncate(starsHeader.length)
            val line = " $user | $message | $stars"
            table.add(line)
        }

        table.joinToString("\n") { "    $it" }
    }

    fun asStartingJobString() = "Ok, give me a second"

    fun asDoneString(measuredTime: Long) = "Done, took $measuredTime ms"

    fun asReminderString(triggerDate: Instant): String {
        val dtf = DateTimeFormatter.ofPattern("'at' HH:mm 'on' dd MMMM yyyy")
                .withZone(ZoneOffset.UTC)

        return "Ok, I will remind you ${dtf.format(triggerDate)} (UTC)"
    }

    fun asStatsString(user: User, stats: UserStats) =
            "Stats for ${user.name} -- " +
                    "**Rep:** ${stats.reputation} - " +
                    "**Questions:** ${stats.nrQuestions} - " +
                    "**Answers:** ${stats.nrAnswers} (ratio ${stats.formattedRatio})"

    fun asRequestedAccessString(user: User, stats: UserStats) =
            "${user.name} requested access. **Rep:** ${stats.reputation} - " +
                    "**Questions:** ${stats.nrQuestions} - " +
                    "**Answers:** ${stats.nrAnswers} (ratio ${stats.formattedRatio})"

    fun asUnknownCommandString(command: String) = "Unknown command '$command'"

    fun asNoAccessString() = "You do not have sufficient access for this command"

    fun asLeavingString() = "Ok, see ya"

    fun asRickRollAlertString() = "Rick roll alert"

    fun asAdamString(adam: String) = adam

    fun asMaukerString(mauker: String) = mauker

    fun asAhmadString(ahmad: String) = ahmad

    fun asBenzPeasantString() = "Looks like your car does not have a star in the front."

    fun asBenzString() = "\uD83C\uDF1F Nice Benz bro! \uD83C\uDF1F"

    fun asDaveString(tiredOf: String) = tiredOf

    fun asBeRightBackString() = "Ok, be right back"

    fun asUpdateErrorString() = "I tried to update, but something went wrong"

    fun asRespondAcceptanceDeadlineExceeded(username: String) = "@${username.asPingName()} " +
            "your access has been revoked because you did not respond"

    fun asRequesteeNotFound() = "I could not find a requestee that needs processing."

}

fun String.asPingName() = this.replace(" ", "")

fun String.sanitize() = this.replace("\r", "").replace("\n", " ").trimEnd()
