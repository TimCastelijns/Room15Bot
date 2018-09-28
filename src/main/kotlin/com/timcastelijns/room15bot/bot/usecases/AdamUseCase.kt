package com.timcastelijns.room15bot.bot.usecases

class AdamUseCase: UseCase<Unit, String> {

    companion object {
        private const val reply = "[\ud83d\udd11](https://stackoverflow.com/keystore)"
    }

    override fun execute(params: Unit): String = reply
}
