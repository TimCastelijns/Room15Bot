package com.timcastelijns.room15bot.eventhandlers

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.timcastelijns.chatexchange.chat.AccessLevel
import com.timcastelijns.chatexchange.chat.AccessLevelChangedEvent
import com.timcastelijns.chatexchange.chat.User
import com.timcastelijns.room15bot.bot.Actor
import com.timcastelijns.room15bot.bot.eventhandlers.AccessLevelChangedEventHandler
import com.timcastelijns.room15bot.bot.usecases.CreateAccessRequestUseCase
import com.timcastelijns.room15bot.bot.usecases.GetUserStatsUseCase
import com.timcastelijns.room15bot.bot.usecases.UserStats
import com.timcastelijns.room15bot.util.MessageFormatter
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import java.time.Instant

class AccessLevelChangedEventHandlerTest {

    private val user = User(3245436, "Some username", 9543, false, false,
            Instant.MIN, Instant.MIN, false, "http://")

    private val userStats = UserStats(user.reputation, 5, 20, "4:16")

    private val getUserStatsUseCase = mock<GetUserStatsUseCase> {
        onBlocking { execute(user) } doReturn userStats
    }

    // TODO verify call
    private val createAccessRequestUseCase = mock<CreateAccessRequestUseCase> {

    }

    private lateinit var handler: AccessLevelChangedEventHandler

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        handler = AccessLevelChangedEventHandler(getUserStatsUseCase, createAccessRequestUseCase, MessageFormatter())
    }

    @Test
    fun testAccessRequest() = runBlocking {
        val event = mock<AccessLevelChangedEvent> {
            on { accessLevel } doReturn AccessLevel.REQUEST
            on { targetUser } doReturn user
         }

        val actor = mock<Actor>()

        handler.handle(event, actor)

        verify(getUserStatsUseCase).execute(user)

        verify(actor).acceptMessage("Some username requested access. **Rep:** 9543 - **Questions:** 5 - **Answers:** 20 (ratio 4:16)")
    }

}
