package com.timcastelijns.room15bot.util

import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CommandParserTest {

    private val parser = CommandParser()

    @Test
    fun testStatsMe() {
        val command = "!stats"
        assertTrue { parser.parse(command).type == CommandType.STATS_ME }
        assertTrue { parser.parse(command).args == null }
    }

    @Test
    fun testStatsUser() {
        val command = "!stats 12345678"
        assertTrue { parser.parse(command).type == CommandType.STATS_USER }
        assertTrue { parser.parse(command).args == "12345678" }
    }

    @Test
    fun testStarsAny() {
        val command = "!stars"
        assertTrue { parser.parse(command).type == CommandType.STARS_ANY }
        assertTrue { parser.parse(command).args == null }
    }

    @Test
    fun testStarsUser() {
        var command = "!stars jon"
        assertTrue { parser.parse(command).type == CommandType.STARS_USER }
        assertTrue { parser.parse(command).args == "jon" }

        // Also test some exceptions like upside down text and spaces and accents.
        command = "!stars Skizo-ozᴉʞS"
        assertTrue { parser.parse(command).type == CommandType.STARS_USER }
        assertTrue { parser.parse(command).args == "Skizo-ozᴉʞS" }

        command = "!stars Félix Gagnon-Grenier"
        assertTrue { parser.parse(command).type == CommandType.STARS_USER }
        assertTrue { parser.parse(command).args == "Félix Gagnon-Grenier" }
    }

    @Test
    fun testRemindMe() {
        val command = "!remindme to implement a new bot feature later"
        assertTrue { parser.parse(command).type == CommandType.REMIND_ME }
        assertTrue { parser.parse(command).args == "to implement a new bot feature later" }
    }

    @Test
    fun testAccept() {
        val command = "!accept Random Username123"
        assertTrue { parser.parse(command).type == CommandType.ACCEPT }
        assertTrue { parser.parse(command).args == "Random Username123"}
    }

    @Test
    fun testReject() {
        val command = "!reject alsorandomnamebutDifferent1"
        assertTrue { parser.parse(command).type == CommandType.REJECT }
        assertTrue { parser.parse(command).args == "alsorandomnamebutDifferent1"}
    }

    @Test
    fun testLeave() {
        val command = "!getlost"
        assertTrue { parser.parse(command).type == CommandType.LEAVE }
        assertTrue { parser.parse(command).args == null }
    }

    @Test
    fun testSyncStars() {
        val command = "!syncstars"
        assertTrue { parser.parse(command).type == CommandType.SYNC_STARS }
        assertTrue { parser.parse(command).args == null }
    }

    @Test
    fun testCasingDoesntMatter() {
        parser.parse("!STATS")
        parser.parse("!STAts 25436")
        parser.parse("!STArs Tim")
        parser.parse("!stARs")
        parser.parse("!reMinDMe bla bla bla")
        parser.parse("!FucKOFf")
        parser.parse("!SYNCstars")
    }

    @Test
    fun testUnknown() {
        val command = "!betterEcho"
        assertFailsWith(IllegalArgumentException::class) {
            parser.parse(command)
        }
    }

}
