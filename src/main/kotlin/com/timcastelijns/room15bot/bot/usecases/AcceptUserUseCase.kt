package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.util.asPingName

class AcceptUserUseCase : UseCase<String, String> {

    companion object {
        private const val rulesUrl = "http://room-15.github.io/"

        private var acceptMessage = "@%s welcome. Please start by reading the [rules]($rulesUrl) and confirm you have read them before saying anything else."
    }

    override fun execute(params: String) = acceptMessage.format(params.asPingName())

}
