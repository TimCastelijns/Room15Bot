package com.timcastelijns.room15bot.usecases

import com.timcastelijns.room15bot.bot.usecases.CfUseCase
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CfUseCaseTest {

    private val possibleReplies = arrayOf(
            "*sigh*",
            "^",
            ":D",
            "true",
            "lmao",
            "haha"
    )

    @Test
    fun testByRandom() {
        val input = null
        assertTrue { possibleReplies.contains(CfUseCase().execute(input)) }
        assertTrue { possibleReplies.contains(CfUseCase().execute(input)) }
        assertTrue { possibleReplies.contains(CfUseCase().execute(input)) }
    }

    @Test
    fun testByIndex() {
        possibleReplies.forEachIndexed { index, reply ->
            val input = "[$index]"
            assertEquals(reply, CfUseCase().execute(input))
        }
    }

    @Test
    fun invalidInputThrows() {
        assertFailsWith(IllegalArgumentException::class) {
            CfUseCase().execute("[a]")
            CfUseCase().execute("[-1]")
        }
    }

    @Test
    fun invalidIndexThrows() {
        assertFailsWith(IllegalArgumentException::class) {
            CfUseCase().execute("[6]")
        }
        assertFailsWith(IllegalArgumentException::class) {
            CfUseCase().execute("[10]")
        }
    }

}
