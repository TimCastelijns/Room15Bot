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
    private val leavePattern = Pattern.compile("!(?i)(shoo|leave|die|getlost|fuckoff)(.+)?")
    private val syncStarsPattern = Pattern.compile("!(?i)syncstars")

    fun parse(rawCommand: String): Command {
        var command: Command? = null

        if (rawCommand.matches(statsMePattern.toRegex())) {
            with(statsMePattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.STATS_ME
                    }
                }
            }
        } else if (rawCommand.matches(statsUserPattern.toRegex())) {
            with(statsUserPattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.STATS_USER
                        args = group(1)
                    }
                }
            }
        } else if (rawCommand.matches(starsAnyPattern.toRegex())) {
            with(starsAnyPattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.STARS_ANY
                    }
                }
            }
        } else if (rawCommand.matches(starsUserPattern.toRegex())) {
            with(starsUserPattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.STARS_USER
                        args = group(1)
                    }
                }
            }
        } else if (rawCommand.matches(remindMePattern.toRegex())) {
            with(remindMePattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.REMIND_ME
                        args = group(1)
                    }
                }
            }
        } else if (rawCommand.matches(leavePattern.toRegex())) {
            with(leavePattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.LEAVE
                    }
                }
            }
        } else if (rawCommand.matches(syncStarsPattern.toRegex())) {
            with(syncStarsPattern.matcher(rawCommand)) {
                if (find()) {
                    command = commandOf {
                        type = CommandType.SYNC_STARS
                    }
                }
            }
        }

        return command ?: throw IllegalArgumentException("Unknown command: $command")
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
