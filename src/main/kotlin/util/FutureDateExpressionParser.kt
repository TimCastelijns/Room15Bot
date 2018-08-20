package util

import java.util.concurrent.TimeUnit

class FutureDateExpressionParser {

    private val secondsIndicators = listOf("seconds", "sec", "secs")
    private val pastDateIndicators = listOf("yesterday", "yday", "last", "past", "ago")
    private val regexPast =     Regex("(-[0-9]+)")

    private val regexMinutes =  Regex("([A-Za-z ']+)?\\d+(\\s+)?m(in|ins)?(.+)?")
    private val regexHours =    Regex("([A-Za-z ']+)?\\d+(\\s+)?h(our|ours)?(.+)?")
    private val regexDays =     Regex("([A-Za-z ']+)?\\d+(\\s+)?d(ay)?(s)?(.+)?")

    private val arbitraries = mapOf(
            "later" to TimeUnit.HOURS.toMillis(4),
            "tomorrow" to TimeUnit.DAYS.toMillis(1),
            "next week" to TimeUnit.DAYS.toMillis(7),
            "next month" to TimeUnit.DAYS.toMillis(30),
            "next year" to TimeUnit.DAYS.toMillis(365)
    )
    private val regexArbitrary = Regex("${arbitraries.keys.joinToString("|")}(.+)?")

    private val regexDigits = Regex("\\d+")

    /**
     * Parses an expression that describes a date in the future and returns the
     * milliseconds that represents it, relative to now.
     *
     * E.g. but not limited to:
     * 12h returns 12 * 3_600_000
     * 30 mins returns 30 * 60_000
     * tomorrow returns 24 * 3_600_000
     */
    fun parse(expression: String): Long {
        validateInput(expression)

        return when {
            expression.matches(regexMinutes) -> {
                val mins = expression.extractValue()
                TimeUnit.MINUTES.toMillis(mins)
            }
            expression.matches(regexHours) -> {
                val hours = expression.extractValue()
                TimeUnit.HOURS.toMillis(hours)
            }
            expression.matches(regexDays) -> {
                val days = expression.extractValue()
                TimeUnit.DAYS.toMillis(days)
            }
            expression.matches(regexArbitrary) -> arbitraries[expression] ?: throw IllegalArgumentException("This syntax is not supported")
            else -> throw IllegalArgumentException("This syntax is not supported")
        }
    }

    private fun validateInput(expression: String) {
        val containsSeconds = secondsIndicators.any { expression.contains(it) }
        if (containsSeconds) {
            throw IllegalArgumentException("Seconds are not supported. Minimum supported unit is minute")
        }

        // Check some obvious past date indicators.
        val containsPastIndicator = pastDateIndicators.any { expression.contains(it) }
        if (containsPastIndicator) {
            throw IllegalArgumentException("This date seems to be in the past")
        }

        if (regexPast.toPattern().matcher(expression).find()) {
            throw IllegalArgumentException("This date seems to be in the past")
        }
    }

    private fun String.extractValue(): Long {
        val matcher = regexDigits.toPattern().matcher(this)
        if (!matcher.find()) {
            throw IllegalArgumentException("There appear to be some digits missing. " +
                    "Have the regex verified, it should check that")
        }

        return matcher.group().toLong()
    }

}