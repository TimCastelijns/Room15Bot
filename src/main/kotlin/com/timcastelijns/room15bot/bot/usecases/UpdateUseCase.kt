package com.timcastelijns.room15bot.bot.usecases

class UpdateUseCase : UseCase<Unit, Unit> {

    companion object {
        private const val UPDATE_SCRIPT_PATH = "./updateself.sh"
    }

    override fun execute(params: Unit) {
        Runtime.getRuntime().exec(UPDATE_SCRIPT_PATH)
    }

}
