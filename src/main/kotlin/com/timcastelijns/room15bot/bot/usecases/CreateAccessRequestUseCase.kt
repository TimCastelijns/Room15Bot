package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.AccessRequestDao

class CreateAccessRequestUseCase(
        private val accessRequestDao: AccessRequestDao
) : UseCase<CreateAccessRequestParams, Unit> {

    override fun execute(params: CreateAccessRequestParams) {
        accessRequestDao.create(
                params.userId,
                params.username
        )
    }

}

data class CreateAccessRequestParams(
        val userId: Long,
        val username: String
)
