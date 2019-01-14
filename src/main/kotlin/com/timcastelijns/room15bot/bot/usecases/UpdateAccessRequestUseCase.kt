package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.AccessRequestDao

class UpdateAccessRequestUseCase(
        private val accessRequestDao: AccessRequestDao
) : UseCase<UpdateAccessRequestParams, Unit> {

    override fun execute(params: UpdateAccessRequestParams) {
        accessRequestDao.update(
                params.userId,
                params.processed,
                params.processedBy,
                params.accessGranted
        )
    }

}

data class UpdateAccessRequestParams(
        val userId: Long,
        val processed: Boolean,
        val processedBy: String,
        val accessGranted: Boolean
)
