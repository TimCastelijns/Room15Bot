package com.timcastelijns.room15bot.util

private const val USER_NAME_PREFIX = "user"

class UserNameValidator {

    fun isValid(userName: String): Boolean {
        return (userName.startsWith(USER_NAME_PREFIX) &&
                userName.removeSpaces()
                        .substring(USER_NAME_PREFIX.length).all { it.isDigit() })
                .not()
    }

}

private fun String.removeSpaces() = this.replace(" ", "")
