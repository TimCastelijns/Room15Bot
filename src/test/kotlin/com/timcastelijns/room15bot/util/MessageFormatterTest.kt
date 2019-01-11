package com.timcastelijns.room15bot.util

import com.timcastelijns.room15bot.bot.usecases.StarsData
import com.timcastelijns.room15bot.bot.usecases.UserStats
import com.timcastelijns.chatexchange.chat.User
import com.timcastelijns.room15bot.data.BuildConfig
import com.timcastelijns.room15bot.data.repositories.StarredMessage
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class MessageFormatterTest {

    private val messageFormatter = MessageFormatter()

    private val shortData = StarredMessage("X",
            "Lol",
            1,
            "")

    private val longData = StarredMessage("A random name",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                    "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
                    "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat",
            750,
            "")

    private val weirdData = StarredMessage("ʇǝǝʞs uoɾ",
            "try {\n" +
                    "            (╯°□°）╯︵ ┻━┻\n" +
                    "        } catch() {\n" +
                    "            ┬─┬\uFEFF | 9 |",
            1,
            "")

    private val skeet = User(
            1,
            "Jon Skeet",
            1_000_000,
            false,
            false,
            Instant.ofEpochMilli(1510000000000),
            Instant.ofEpochMilli(1510000000000),
            false,
            "http://example.org"
    )

    @Test
    fun testHelp() {
        val output = messageFormatter.asHelpString()

        assertEquals("You can find information on what I can do [here](https://github.com/TimCastelijns/Room15Bot#usage)", output)
    }

    @Test
    fun testStatus() {
        val buildConfig = BuildConfig("0.1", "master", "abcd1234", "01-01-1970 08:00")
        val output = messageFormatter.asStatusString(buildConfig, 200)

        assertEquals("Online since 01-01-1970 08:00 (UTC). Running on master@abcd1234. Uptime: 200h", output)
    }

    @Test
    fun testShortData() {
        val data = StarsData(listOf(shortData), 1, 1)

        val expected = """
             User   | Message (1)                                      | Stars (1)
            ----------------------------------------------------------------------
             X      | Lol                                              | 1
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testLongData() {
        val data = StarsData(listOf(longData), 13436, 999)

        val expected = """
             User       | Message (13436)                                  | Stars (999)
            ----------------------------------------------------------------------------
             A random n | Lorem ipsum dolor sit amet, consectetur adipisci | 750
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testWeirdData() {
        val data = StarsData(listOf(weirdData), 1, 1)

        val expected = """
             User      | Message (1)                                      | Stars (1)
            -------------------------------------------------------------------------
             ʇǝǝʞs uoɾ | try {             (╯°□°）╯︵ ┻━┻         } catch() | 1
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testMixedData() {
        val data = StarsData(listOf(shortData, longData, weirdData), 3, 752)

        val expected = """
             User       | Message (3)                                      | Stars (752)
            ----------------------------------------------------------------------------
             X          | Lol                                              | 1
             A random n | Lorem ipsum dolor sit amet, consectetur adipisci | 750
             ʇǝǝʞs uoɾ  | try {             (╯°□°）╯︵ ┻━┻         } catch() | 1
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testReminderFormat() {
        val triggerDate = Instant.ofEpochMilli(1510000000000)

        val expected = "Ok, I will remind you at 20:26 on 06 November 2017 (UTC)"
        val output = messageFormatter.asReminderString(triggerDate)

        assertEquals(expected, output)
    }

    @Test
    fun testStatsFormat() {
        val expected = "Stats for Jon Skeet -- **Rep:** 50000 - **Questions:** 0 - **Answers:** 800 (ratio 4:3200)"
        val output = messageFormatter.asStatsString(
                skeet, UserStats(50000, 0, 800, "4:3200"))

        assertEquals(expected, output)
    }

    @Test
    fun testRequestedAccessFormat() {
        val expected = "Jon Skeet requested access. **Rep:** 10000 - **Questions:** 4 - **Answers:** 500 (ratio 4:500)"
        val output = messageFormatter.asRequestedAccessString(
                skeet, UserStats(10000, 4, 500, "4:500"))

        assertEquals(expected, output)
    }

    @Test
    fun testBeRightBackFormat() {
        val expected = "Ok, be right back"
        val output = messageFormatter.asBeRightBackString()

        assertEquals(expected, output)
    }

    @Test
    fun testBenz() {
        val outputBenz = messageFormatter.asBenzString()
        assertEquals("\uD83C\uDF1F Nice Benz bro! \uD83C\uDF1F", outputBenz)
    }

    @Test
    fun testBenzPeasant() {
        val outputPeasants = messageFormatter.asBenzPeasantString()
        assertEquals("Looks like your car does not have a star in the front.", outputPeasants)
    }

    @Test
    fun testRequesteeNotFound() {
        val expected = "I could not find a user that recently requested access"
        val output = messageFormatter.asRequesteeNotFound()

        assertEquals(expected, output)
    }

}
