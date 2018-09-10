package com.timcastelijns.room15bot.usecases

import com.timcastelijns.room15bot.bot.usecases.MaukerUseCase
import org.junit.Test
import kotlin.test.assertEquals

class MaukerUseCaseTest {

    private val reply = "Mauker, plz."

    @Test
    fun testMaukerReply() {
        assertEquals(reply, MaukerUseCase().execute(Unit))
    }
}
