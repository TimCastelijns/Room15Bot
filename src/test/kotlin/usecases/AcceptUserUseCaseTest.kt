package usecases

import bot.usecases.AcceptUserUseCase
import org.junit.Test
import kotlin.test.assertEquals

class AcceptUserUseCaseTest {

    @Test
    fun testOutputIsCorrect() {
        val input = "shog9"
        val expectedOutput = "@shog9 welcome. Please start by reading the [rules](http://room-15.github.io/) and confirm you have read them before saying anything else."

        assertEquals(expectedOutput, AcceptUserUseCase().execute(input))
    }

}
