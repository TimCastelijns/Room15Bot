package com.timcastelijns.room15bot.usecases

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.timcastelijns.room15bot.bot.usecases.UpdateUseCase
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class UpdateUseCaseTest {

    private val updateScriptPath = "./updateself.sh"

    private val runtime = mock<Runtime> {
        on { exec(updateScriptPath) } doReturn (mock<Process>())
    }

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testCallsUpdateScript() {
        UpdateUseCase(runtime).execute(Unit)
        verify(runtime).exec(updateScriptPath)
    }

}
