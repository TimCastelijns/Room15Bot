package com.timcastelijns.room15bot.usecases

import com.timcastelijns.room15bot.bot.usecases.MaukerUseCase
import org.junit.Test
import kotlin.test.assertTrue

class MaukerUseCaseTest {

    private val reply = "Mauker, plz."

    @Test
    fun testMaukerReply() {
        assertTrue { reply == MaukerUseCase().execute(null) }
    }

    @Test
    fun testMaukerWithArguments() {
        val input = "Whatever"
        assertTrue { reply == MaukerUseCase().execute(input) }
    }
}