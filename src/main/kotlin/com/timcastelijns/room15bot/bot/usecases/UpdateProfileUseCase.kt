package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.UserDao
import com.timcastelijns.room15bot.data.db.UserProfileDao

class UpdateProfileUseCase(
        private val userDao: UserDao,
        private val userProfileDao: UserProfileDao
) : UseCase<UpdateProfileCommandParams, Unit>{

    override fun execute(params: UpdateProfileCommandParams) {
        val user = userDao.getById(params.userId)!!

        // TODO handle empty brackets
        val args = params.commandArgs.trimStart('[').trimEnd(']').split("] [")

        val nickname = args[0]
        val age = args[1].toIntOrNull()

        if (user.profileId == null) {
            // No profile yet, create one first.
            userProfileDao.create(user, nickname, age)
        }

        // TODO implement update profile
    }

}

data class UpdateProfileCommandParams(
        val userId: Long,
        val commandArgs: String
)
