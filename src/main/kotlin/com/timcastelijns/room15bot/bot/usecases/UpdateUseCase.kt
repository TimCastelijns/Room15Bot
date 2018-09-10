package com.timcastelijns.room15bot.bot.usecases

class UpdateUseCase(
        private val runtime: Runtime
) : UseCase<Unit, Unit> {

    companion object {
        private const val UPDATE_SCRIPT_PATH = "./updateself.sh"
    }

    override fun execute(params: Unit) {
        runtime.exec(UPDATE_SCRIPT_PATH)
    }

}
