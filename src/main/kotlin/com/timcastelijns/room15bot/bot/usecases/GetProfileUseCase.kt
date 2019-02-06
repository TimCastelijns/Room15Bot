package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.UserProfile
import com.timcastelijns.room15bot.data.db.UserDao

class GetProfileUseCase(
        private val userDao: UserDao
): UseCase<Long, UserProfile>{

    override fun execute(params: Long): UserProfile {
        val user = userDao.getById(params)!!
        return userDao.getProfile(user)!!
    }

}
