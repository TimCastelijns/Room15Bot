package com.timcastelijns.room15bot.bot.usecases

class MaukerUseCase: UseCase<String?, String> {

    companion object {
        private const val reply = "Mauker, plz."
    }

    override fun execute(params: String?): String = reply
}