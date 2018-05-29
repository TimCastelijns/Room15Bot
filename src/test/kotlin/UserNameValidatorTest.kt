import org.junit.Test
import util.UserNameValidator
import kotlin.test.assertFalse

class UserNameValidatorTest {

    val userNameValidator = UserNameValidator()

    @Test
    fun testValidNameIsValid() {
        assert(userNameValidator.isValid("Jon Skeet"))
        assert(userNameValidator.isValid("Community"))
    }

    @Test
    fun testInvalidNameIsInvalid() {
        assertFalse(userNameValidator.isValid("user2354367"))
        assertFalse(userNameValidator.isValid("user 2354367"))
        assertFalse(userNameValidator.isValid("user    "))
    }

}
