package com.timcastelijns.room15bot.bot.usecases

import com.timcastelijns.room15bot.util.asPingName

class RejectUserUseCase : UseCase<String, String> {

    companion object {
        private const val rulesUrl = "http://room-15.github.io/"

        private var rejectMessage = "@%s you currently do not meet the requirements to chat here. You can find our requirements in the [rules]($rulesUrl)."
    }

    override fun execute(params: String) = rejectMessage.format(params.asPingName())

}
