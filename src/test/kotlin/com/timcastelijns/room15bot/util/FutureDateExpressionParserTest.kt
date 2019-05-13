package com.timcastelijns.room15bot.util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FutureDateExpressionParserTest {

    private val parser = FutureDateExpressionParser()

    @Test
    fun testDateInPastThrowsIllegalArgument() {
        assertFailsWith(IllegalArgumentException::class) { parser.parse("yesterday") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("yday") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("last week") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("past sunday") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("2 weeks ago") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("-1 hour") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("-20 mins") }
        assertFailsWith(IllegalArgumentException::class) { parser.parse("-365 days") }
    }

    @Ignore
    @Test
    fun testDateTooFarInTheFutureThrowsIllegalArgument() {
        assertFailsWith(IllegalArgumentException::class) { parser.parse("9223372036854775806h") }
    }

    @Ignore // 'm' is currently defaulting to minutes and no other ambiguous units are used.
    @Test
    fun testAmbiguousUnitThrowsIllegalArgument() {
        assertFailsWith(IllegalArgumentException::class) { parser.parse("1m") }
    }

    @Test
    fun testMinutesAreParsedCorrectly() {
        assertTrue { parser.parse("1min") == TimeUnit.MINUTES.toMillis(1) }
        assertTrue { parser.parse("1m") == TimeUnit.MINUTES.toMillis(1) }
        assertTrue { parser.parse("5 min") == TimeUnit.MINUTES.toMillis(5) }
        assertTrue { parser.parse("700mins") == TimeUnit.MINUTES.toMillis(700) }
    }

    @Test
    fun testHoursAreParsedCorrectly() {
        assertTrue { parser.parse("1h") == TimeUnit.HOURS.toMillis(1) }
        assertTrue { parser.parse("24h") == TimeUnit.HOURS.toMillis(24) }
        assertTrue { parser.parse("24 h") == TimeUnit.HOURS.toMillis(24) }
        assertTrue { parser.parse("600h") == TimeUnit.HOURS.toMillis(600) }
        assertTrue { parser.parse("5 hours") == TimeUnit.HOURS.toMillis(5) }
        assertTrue { parser.parse("6 hour") == TimeUnit.HOURS.toMillis(6) }
        assertTrue { parser.parse("in 1 hour") == TimeUnit.HOURS.toMillis(1) }
        assertTrue { parser.parse("in about 6 hours") == TimeUnit.HOURS.toMillis(6) }
        assertTrue { parser.parse("in about 24 hours from now") == TimeUnit.HOURS.toMillis(24) }
        assertTrue { parser.parse("let's do 10 hours from now") == TimeUnit.HOURS.toMillis(10) }
    }

    @Test
    fun testDaysAreParsedCorrectly() {
        assertTrue { parser.parse("1d") == TimeUnit.DAYS.toMillis(1) }
        assertTrue { parser.parse("1day") == TimeUnit.DAYS.toMillis(1) }
        assertTrue { parser.parse("1 d") == TimeUnit.DAYS.toMillis(1) }
        assertTrue { parser.parse("1 day") == TimeUnit.DAYS.toMillis(1) }
        assertTrue { parser.parse("500 days") == TimeUnit.DAYS.toMillis(500) }
        assertTrue { parser.parse("2days") == TimeUnit.DAYS.toMillis(2) }
    }

    @Test
    fun testWeeksAreParsedCorrectly() {
        assertTrue { parser.parse("1w") == TimeUnit.DAYS.toMillis(7) }
        assertTrue { parser.parse("1week") == TimeUnit.DAYS.toMillis(7) }
        assertTrue { parser.parse("1 w") == TimeUnit.DAYS.toMillis(7) }
        assertTrue { parser.parse("1 week") == TimeUnit.DAYS.toMillis(7) }
        assertTrue { parser.parse("500 weeks") == TimeUnit.DAYS.toMillis(500 * 7) }
        assertTrue { parser.parse("2weeks") == TimeUnit.DAYS.toMillis(2 * 7) }
    }

    @Test
    fun testMonthsAreParsedCorrectly() {
        fun monthsToMilli(months: Long): Long {
            // Check how many days away 'today + months' is.
            val now = DateTime.now(DateTimeZone.UTC)
            val then = now.plusMonths(months.toInt())
            val days = Days.daysBetween(now, then).days
            return TimeUnit.DAYS.toMillis(days.toLong())
        }

        assertTrue { parser.parse("1mon") == monthsToMilli(1) }
        assertTrue { parser.parse("1month") == monthsToMilli(1) }
        assertTrue { parser.parse("1 mon") == monthsToMilli(1) }
        assertTrue { parser.parse("1 month") == monthsToMilli(1) }
        assertTrue { parser.parse("500 months") == monthsToMilli(500) }
        assertTrue { parser.parse("2months") == monthsToMilli(2) }
    }

    @Test
    fun testArbitrayExpressionsAreParsedCorrectly() {
        assertTrue { parser.parse("later") == TimeUnit.HOURS.toMillis(4) }
        assertTrue { parser.parse("tomorrow") == TimeUnit.DAYS.toMillis(1) }
        assertTrue { parser.parse("next week") == TimeUnit.DAYS.toMillis(7) }
        assertTrue { parser.parse("next month") == TimeUnit.DAYS.toMillis(30) }
        assertTrue { parser.parse("next year") == TimeUnit.DAYS.toMillis(365) }
    }

}
