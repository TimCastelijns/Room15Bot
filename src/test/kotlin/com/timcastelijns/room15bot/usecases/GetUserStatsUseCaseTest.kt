package com.timcastelijns.room15bot.usecases

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.timcastelijns.chatexchange.chat.User
import com.timcastelijns.room15bot.bot.usecases.GetUserStatsUseCase
import com.timcastelijns.room15bot.data.repositories.UserStatsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import java.time.Instant
import kotlin.test.assertEquals

class GetUserStatsUseCaseTest {

    private val userStatsRepository = mock<UserStatsRepository> {
        onBlocking { getNumberOfQuestions(1) } doReturn 0
        onBlocking { getNumberOfAnswers(1) } doReturn 0

        onBlocking { getNumberOfQuestions(2) } doReturn 8
        onBlocking { getNumberOfAnswers(2) } doReturn 2

        onBlocking { getNumberOfQuestions(3) } doReturn 4
        onBlocking { getNumberOfAnswers(3) } doReturn 234
    }

    private lateinit var getUserStatsUseCase: GetUserStatsUseCase

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        getUserStatsUseCase = GetUserStatsUseCase(userStatsRepository)
    }

    @Test
    fun testFormattedRatioIsCorrect() = runBlocking {
        var user = givenUserWithId(1)
        var formattedRatio = "4:0"

        var output = getUserStatsUseCase.execute(user)
        assertEquals(formattedRatio, output.formattedRatio)

        user = givenUserWithId(2)
        formattedRatio = "4:1"

        output = getUserStatsUseCase.execute(user)
        assertEquals(formattedRatio, output.formattedRatio)

        user = givenUserWithId(3)
        formattedRatio = "4:234"

        output = getUserStatsUseCase.execute(user)
        assertEquals(formattedRatio, output.formattedRatio)
    }

    private fun givenUserWithId(id: Long) =
            User(id, "Some username", 1, false, false,
                    Instant.MIN, Instant.MIN, false, "http://")
}
