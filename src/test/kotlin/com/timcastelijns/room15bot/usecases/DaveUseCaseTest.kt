package com.timcastelijns.room15bot.usecases

import com.timcastelijns.room15bot.bot.usecases.DaveUseCase
import org.junit.Test
import kotlin.test.assertEquals

class DaveUseCaseTest {

    private val reply = "[Tired of your shit, Dave](https://www.youtube.com/watch?v=oHg5SJYRHA0)"

    @Test
    fun testDaveReply() {
        assertEquals(reply, DaveUseCase().execute(Unit))
    }
}
