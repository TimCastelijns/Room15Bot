package usecases

import bot.usecases.RejectUserUseCase
import org.junit.Test
import kotlin.test.assertEquals

class RejectUserUseCaseTest {

    @Test
    fun testOutputIsCorrect() {
        val input = "shog9"
        val expectedOutput = "@shog9 you do not meet the requirements to chat here. You can find our requirements in the [rules](http://room-15.github.io/)."

        assertEquals(expectedOutput, RejectUserUseCase().execute(input))
    }

}