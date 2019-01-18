package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.db.AccessRequestDao

class UpdateAccessRequestUseCase(
        private val accessRequestDao: AccessRequestDao
) : UseCase<UpdateAccessRequestParams, Unit> {

    override fun execute(params: UpdateAccessRequestParams) {
        with(params) {
            when {
                processed != null -> accessRequestDao.updateProcessed(
                        params.userId,
                        processed,
                        processedBy!!,
                        granted!!
                )
                shouldMonitor != null -> accessRequestDao.updateShouldMonitor(
                        params.userId,
                        shouldMonitor
                )
                revoked != null -> accessRequestDao.updateRevoked(
                        params.userId,
                        revoked
                )
            }
        }
    }

}

data class UpdateAccessRequestParams(
        val userId: Long,
        val processed: Boolean? = null,
        val processedBy: String? = null,
        val granted: Boolean? = null,
        val revoked: Boolean? = null,
        val shouldMonitor: Boolean? = null
)
