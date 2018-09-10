package com.timcastelijns.room15bot.bot.usecases

class MaukerUseCase: UseCase<Unit, String> {

    companion object {
        private const val reply = "Mauker, plz."
    }

    override fun execute(params: Unit): String = reply
}