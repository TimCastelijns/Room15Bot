package com.timcastelijns.room15bot

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BotTest {

    @Test
    fun testRequesteeUnableToChatDetectedCorrectly() {
        val regex = Regex("(.+) requested access\\. Rep: ([1-9]|1[0-9]) (?:.+)")

        listOf(
                Pair("Aneesh P V", 1),
                Pair("guy", 19),
                Pair("another guy", 20)
        ).forEach {
            val message = "${it.first} requested access. Rep: ${it.second} - Questions: 0 - Answers: 0 (ratio 4:0)"

            val matcher = regex.toPattern().matcher(message)
            val found = matcher.find()

            if (it.second < 20) {
                assertTrue { found }

                assertEquals(2, matcher.groupCount())
                assertEquals(it.first, matcher.group(1))
                assertEquals(it.second, matcher.group(2).toInt())
            } else {
                assertFalse { found }
            }
        }
    }

}
