package com.timcastelijns.room15bot.bot.usecases

import java.util.*
import java.util.regex.Pattern

class CfUseCase : UseCase<String?, String> {

    private val cfIndexPattern = Pattern.compile("\\[(\\d+)\\]")

    private val possibleReplies = arrayOf(
            "*sigh*",
            "^",
            ":D",
            "true",
            "lmao",
            "haha"
    )

    override fun execute(params: String?) =
            if (params == null) {
                possibleReplies.random()
            } else {
                val matcher = cfIndexPattern.matcher(params)
                if (!matcher.find()) {
                    throw IllegalArgumentException("This input seems to be invalid")
                }
                val index = matcher.group(1).toInt()
                possibleReplies[index]
            }

}

fun <T> Array<T>.random(): T = get(Random().nextInt(size))
