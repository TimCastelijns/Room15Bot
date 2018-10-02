package com.timcastelijns.room15bot.bot.usecases

class DaveUseCase: UseCase<Unit, String> {

    companion object {
        private const val reply = "[Tired of your shit, Dave](https://www.youtube.com/watch?v=oHg5SJYRHA0)"
    }

    override fun execute(params: Unit): String = reply
}
