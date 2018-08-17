package util

import bot.usecases.StarsData
import com.timcastelijns.chatexchange.chat.User
import data.repositories.StarredMessage
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
    fun testShortData() {
        val data = StarsData(listOf(shortData), 1, 1)

        val expected = """
             User   | Message (1)                                      | Stars (1) | Link
            -----------------------------------------------------------------------------
             X      | Lol                                              | 1         |
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testLongData() {
        val data = StarsData(listOf(longData), 13436, 999)

        val expected = """
             User       | Message (13436)                                  | Stars (999) | Link
            -----------------------------------------------------------------------------------
             A random n | Lorem ipsum dolor sit amet, consectetur adipisci | 750         |
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testWeirdData() {
        val data = StarsData(listOf(weirdData), 1, 1)

        val expected = """
             User      | Message (1)                                      | Stars (1) | Link
            --------------------------------------------------------------------------------
             ʇǝǝʞs uoɾ | try {             (╯°□°）╯︵ ┻━┻         } catch() | 1         |
        """.replaceIndent("    ")

        val output = messageFormatter.asTableString(data)

        assertEquals(expected, output)
    }

    @Test
    fun testMixedData() {
        val data = StarsData(listOf(shortData, longData, weirdData), 3, 752)

        val expected = """
             User       | Message (3)                                      | Stars (752) | Link
            -----------------------------------------------------------------------------------
             X          | Lol                                              | 1           |
             A random n | Lorem ipsum dolor sit amet, consectetur adipisci | 750         |
             ʇǝǝʞs uoɾ  | try {             (╯°□°）╯︵ ┻━┻         } catch() | 1           |
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
        val expected = "Stats for Jon Skeet -- many rep such wow 30k answers amaze"
        val output = messageFormatter.asStatsString(
                skeet, "many rep such wow 30k answers amaze")

        assertEquals(expected, output)
    }

    @Test
    fun testRequestedAccessFormat() {
        val expected = "Jon Skeet requested access. 1000k rep, few questions, many answers (nice ratio)"
        val output = messageFormatter.asRequestedAccessString(
                skeet, "1000k rep, few questions, many answers (nice ratio)")

        assertEquals(expected, output)
    }

}
