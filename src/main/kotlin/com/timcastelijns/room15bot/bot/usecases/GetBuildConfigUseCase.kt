package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.data.BuildConfig
import com.timcastelijns.room15bot.data.repositories.ConfigRepository

class GetBuildConfigUseCase(
        private val configRepository: ConfigRepository
) : UseCase<Unit, BuildConfig> {

    override fun execute(params: Unit) = configRepository.getBuildConfig()

}
