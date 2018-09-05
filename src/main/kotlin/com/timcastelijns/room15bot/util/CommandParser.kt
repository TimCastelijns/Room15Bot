package com.timcastelijns.room15bot.util

import java.util.regex.Pattern

class CommandParser {

    private val anyUsernameRegex = Regex("(.+)")

    private val statsMePattern = Pattern.compile("!(?i)stats")
    private val statsUserPattern = Pattern.compile("!(?i)stats\\s(\\d+)")
    private val starsAnyPattern = Pattern.compile("!(?i)stars")
    private val starsUserPattern = Pattern.compile("!(?i)stars\\s$anyUsernameRegex")
    private val remindMePattern = Pattern.compile("!(?i)remindme\\s(.+)")
    private val cfPattern = Pattern.compile("!(?i)cf(\\[-?(\\d+)\\])?")

    private val acceptPattern = Pattern.compile("!(?i)accept\\s$anyUsernameRegex")
    private val rejectPattern = Pattern.compile("!(?i)reject\\s$anyUsernameRegex")
    private val leavePattern = Pattern.compile("!(?i)(?:shoo|leave|die|getlost|fuckoff)")
    private val syncStarsPattern = Pattern.compile("!(?i)syncstars")

    private val needsName = mapOf<Pattern, CommandType>(
            statsMePattern to CommandType.STATS_ME,
            statsUserPattern to CommandType.STATS_USER,
            starsAnyPattern to CommandType.STARS_ANY,
            starsUserPattern to CommandType.STARS_USER,
            remindMePattern to CommandType.REMIND_ME,
            cfPattern to CommandType.CF,
            acceptPattern to CommandType.ACCEPT,
            rejectPattern to CommandType.REJECT,
            leavePattern to CommandType.LEAVE,
            syncStarsPattern to CommandType.SYNC_STARS
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
    STATS_ME,
    STATS_USER,
    STARS_ANY,
    STARS_USER,
    REMIND_ME,
    CF,

    // Elevated access commands.
    ACCEPT,
    REJECT,
    LEAVE,
    SYNC_STARS
}
