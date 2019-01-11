package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.UserProfileDao

class CreateUserProfileUseCase(
        private val userProfileDao: UserProfileDao
): UseCase<CreateUserProfileParams, Unit> {

    override fun execute(params: CreateUserProfileParams) {
        userProfileDao.create(
                params.userId,
                params.username
        )
    }

}

data class CreateUserProfileParams(
        val userId: Long,
        val username: String
)
