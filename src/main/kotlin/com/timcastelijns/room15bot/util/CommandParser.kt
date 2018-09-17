package com.timcastelijns.room15bot.util

import org.intellij.lang.annotations.Language
import java.util.regex.Pattern

class CommandParser {

    private val anyUsernameRegex = Regex("(.+)")

    private val benzPattern = caseInsensitivePattern("!\\uD83D\\uDE97")
    private val helpPattern = caseInsensitivePattern("!(?:help|commands|usage)")
    private val statusPattern = caseInsensitivePattern("!(?:status|alive)")
    private val statsMePattern = caseInsensitivePattern("!stats")
    private val statsUserPattern = caseInsensitivePattern("!stats\\s(\\d+)")
    private val starsAnyPattern = caseInsensitivePattern("!stars")
    private val starsUserPattern = caseInsensitivePattern("!stars\\s$anyUsernameRegex")
    private val remindMePattern = caseInsensitivePattern("!remindme\\s(.+)")
    private val cfPattern = caseInsensitivePattern("!cf(\\[-?(\\d+)\\])?")
    private val maukerPattern = caseInsensitivePattern("!mauker")

    private val acceptPattern = caseInsensitivePattern("!accept\\s$anyUsernameRegex")
    private val rejectPattern = caseInsensitivePattern("!reject\\s$anyUsernameRegex")
    private val leavePattern = caseInsensitivePattern("!(?:shoo|leave|die|getlost|fuckoff|ahmad)")
    private val syncStarsPattern = caseInsensitivePattern("!syncstars")
    private val updatePattern = caseInsensitivePattern("!update")

    private val needsName = mapOf<Pattern, CommandType>(
            benzPattern to CommandType.BENZ,
            helpPattern to CommandType.HELP,
            statusPattern to CommandType.STATUS,
            statsMePattern to CommandType.STATS_ME,
            statsUserPattern to CommandType.STATS_USER,
            starsAnyPattern to CommandType.STARS_ANY,
            starsUserPattern to CommandType.STARS_USER,
            remindMePattern to CommandType.REMIND_ME,
            cfPattern to CommandType.CF,
            maukerPattern to CommandType.MAUKER,
            acceptPattern to CommandType.ACCEPT,
            rejectPattern to CommandType.REJECT,
            leavePattern to CommandType.LEAVE,
            syncStarsPattern to CommandType.SYNC_STARS,
            updatePattern to CommandType.UPDATE
    )

    fun parse(rawCommand: String): Command {
        val matcher = needsName.keys.firstOrNull {
            rawCommand.matches(it.toRegex())
        }?.matcher(rawCommand) ?: throw IllegalArgumentException("Unknown command: $rawCommand")

        var command: Command? = null
        with(matcher) {
            if (find()) {
                command = commandOf {
                    type = needsName[matcher.pattern()]!!
                    args = if (groupCount() >= 1) group(1) else null
                }
            }
        }

        return command ?: throw IllegalArgumentException("Unknown command: $rawCommand")
    }

    private fun caseInsensitivePattern(@Language("RegExp") regex: String) =
            Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
}

class Command private constructor(
        val type: CommandType,
        val args: String?
) {

    class Builder {
        lateinit var type: CommandType
        var args: String? = null

        fun build() = Command(type, args)
    }
}

fun commandOf(block: Command.Builder.() -> Unit) =
        Command.Builder().apply(block).build()

enum class CommandType {
    // User commands.
    BENZ,
    HELP,
    STATUS,
    STATS_ME,
    STATS_USER,
    STARS_ANY,
    STARS_USER,
    REMIND_ME,
    CF,
    MAUKER,

    // Elevated access commands.
    ACCEPT,
    REJECT,
    LEAVE,
    SYNC_STARS,
    UPDATE
}
