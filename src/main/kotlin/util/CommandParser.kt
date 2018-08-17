package util

import java.util.regex.Pattern

class CommandParser {

    // User commands.
    private val statsMePattern = Pattern.compile("!(?i)stats")
    private val statsUserPattern = Pattern.compile("!(?i)stats\\s(\\d+)")
    private val starsAnyPattern = Pattern.compile("!(?i)stars")
    private val starsUserPattern = Pattern.compile("!(?i)stars\\s([\\-\\p{L} ]+)")
    private val remindMePattern = Pattern.compile("!(?i)remindme\\s(.+)")

    // Elevated access commands.
    private val leavePattern = Pattern.compile("!(?i)(?:shoo|leave|die|getlost|fuckoff)")
    private val syncStarsPattern = Pattern.compile("!(?i)syncstars")

    private val needsName = mapOf<Pattern, CommandType>(
            statsMePattern to CommandType.STATS_ME,
            statsUserPattern to CommandType.STATS_USER,
            starsAnyPattern to CommandType.STARS_ANY,
            starsUserPattern to CommandType.STARS_USER,
            remindMePattern to CommandType.REMIND_ME,
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
    STATS_ME,
    STATS_USER,
    STARS_ANY,
    STARS_USER,
    REMIND_ME,

    LEAVE,
    SYNC_STARS
}
