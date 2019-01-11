package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.UserProfileDao

class UpdateUserProfileUseCase(private val userProfileDao: UserProfileDao
): UseCase<UpdateUserProfileParams, Unit> {

    override fun execute(params: UpdateUserProfileParams) {
        val args = params.command.trim { it in "[]" }.split("] [")

        val nickname = args[0]
        val age = args[1].toInt()
        val location = args[2]
        val skills = args[3]
        val title = args[4]

        userProfileDao.update(
                params.userId,
                nickname,
                age,
                location,
                skills,
                title
        )
    }

}

data class UpdateUserProfileParams(
        val userId: Long,
        val command: String
)
