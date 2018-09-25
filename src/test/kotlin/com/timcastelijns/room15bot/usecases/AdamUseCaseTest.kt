package com.timcastelijns.room15bot.usecases

import com.timcastelijns.room15bot.bot.usecases.AdamUseCase
import org.junit.Test
import kotlin.test.assertEquals

class AdamUseCaseTest {

    private val reply = "\ud83d\udd11"

    @Test
    fun testAdamReply() {
        assertEquals(reply, AdamUseCase().execute(Unit))
    }
}
