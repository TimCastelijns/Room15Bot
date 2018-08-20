package usecases

import bot.usecases.RejectUserUseCase
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RejectUserUseCaseTest {

    @Test
    fun testOutputIsCorrect() {
        val input = "shog9"
        val expectedOutput = "@shog9 you currently do not meet the requirements to chat here. You can find our requirements in the [rules](http://room-15.github.io/)."

        assertEquals(expectedOutput, RejectUserUseCase().execute(input))
    }

    @Test
    fun testSpacesAreRemovedFromName() {
        val input = "jon skeet"

        assertTrue { RejectUserUseCase().execute(input).startsWith("@jonskeet") }
    }

}