package com.timcastelijns.room15bot.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CommandParserTest {

    private val parser = CommandParser()

    @Test
    fun testHelp() {
        var command = "!help"
        assertTrue { parser.parse(command).type == CommandType.HELP }
        assertTrue { parser.parse(command).args == null }

        command = "!commands"
        assertTrue { parser.parse(command).type == CommandType.HELP }
        assertTrue { parser.parse(command).args == null }

        command = "!usage"
        assertTrue { parser.parse(command).type == CommandType.HELP }
        assertTrue { parser.parse(command).args == null }
    }

    @Test
    fun testStatus() {
        val commands = listOf("!status", "!alive")
        commands.forEach {
            assertEquals(CommandType.STATUS, parser.parse(it).type)
            assertNull(parser.parse(it).args)
        }
    }

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

        command = "!stars payne911"
        assertTrue { parser.parse(command).type == CommandType.STARS_USER }
        assertTrue { parser.parse(command).args == "payne911" }
    }

    @Test
    fun testRemindMe() {
        val command = "!remindme to implement a new bot feature later"
        assertTrue { parser.parse(command).type == CommandType.REMIND_ME }
        assertTrue { parser.parse(command).args == "to implement a new bot feature later" }
    }

    @Test
    fun testProfile() {
        val command = "!profile"
        assertTrue { parser.parse(command).type == CommandType.PROFILE }
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testEditProfile() {
        val command = "!editprofile [tim] [29]"
        assertTrue { parser.parse(command).type == CommandType.UPDATE_PROFILE }
        assertTrue { parser.parse(command).args == "[tim] [29]" }
    }

    @Test
    fun testNorsemenReference() {
        var command = "!nm"
        assertEquals(CommandType.NORSEMEN_REFERENCE, parser.parse(command).type)
        assertNull(parser.parse(command).args)

        command = "!nm shitting log"
        assertEquals(CommandType.NORSEMEN_REFERENCE, parser.parse(command).type)
        assertEquals("shitting log", parser.parse(command).args)
    }

    @Test
    fun testAdam() {
        val command = "!adam"
        assertTrue { parser.parse(command).type == CommandType.ADAM }
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testMauker() {
        val command = "!mauker"
        assertTrue { parser.parse(command).type == CommandType.MAUKER }
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testAhmad() {
        val command = "!ahmad"
        assertTrue { parser.parse(command).type == CommandType.AHMAD }
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testBenz() {
        val command = ("!\uD83D\uDE97")
        assertTrue { parser.parse(command).type == CommandType.BENZ }
        assertTrue { parser.parse(command).args == null }
    }

    @Test
    fun testDave() {
        val command = "!dave"
        assertTrue { parser.parse(command).type == CommandType.DAVE }
        assertNull(parser.parse(command).args)
    }


    @Test
    fun testAccept() {
        var command = "!accept Random Username123"
        assertTrue { parser.parse(command).type == CommandType.ACCEPT }
        assertTrue { parser.parse(command).args == "Random Username123" }

        command = "!accept"
        assertTrue { parser.parse(command).type == CommandType.ACCEPT }
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testReject() {
        var command = "!reject alsorandomnamebutDifferent1"
        assertTrue { parser.parse(command).type == CommandType.REJECT }
        assertTrue { parser.parse(command).args == "alsorandomnamebutDifferent1" }

        command = "!reject"
        assertTrue { parser.parse(command).type == CommandType.REJECT }
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testLeave() {
        val command = "!leave"
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
    fun testUpdate() {
        val command = "!update"
        assertEquals(CommandType.UPDATE, parser.parse(command).type)
        assertNull(parser.parse(command).args)
    }

    @Test
    fun testCasingDoesntMatter() {
        parser.parse("!STATS")
        parser.parse("!STAts 25436")
        parser.parse("!STArs Tim")
        parser.parse("!stARs")
        parser.parse("!reMinDMe bla bla bla")
        parser.parse("!LEAve")
        parser.parse("!SYNCstars")
        parser.parse("!AdAm")
        parser.parse("!MaUkEr")
        parser.parse("!AhMaD")
        parser.parse("!DaVe")
        parser.parse("!STatuS")
        parser.parse("!UpDaTe")
    }

    @Test
    fun testUnknown() {
        val command = "!betterEcho"
        assertFailsWith(IllegalArgumentException::class) {
            parser.parse(command)
        }
    }

}
