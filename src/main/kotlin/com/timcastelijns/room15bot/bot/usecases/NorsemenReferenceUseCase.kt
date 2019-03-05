package com.timcastelijns.room15bot.bot.usecases

import kotlin.random.Random

class NorsemenReferenceUseCase : UseCase<String?, NorsemenReference> {

    override fun execute(params: String?): NorsemenReference =
            if (params == null) {
                if (Random.nextInt(2) == 0) {
                    NorsemenReference(textQuotes.random(), ReferenceType.QUOTE)
                } else {
                    NorsemenReference(imageQuotes.values.random(), ReferenceType.IMAGE)
                }
            } else {
                if (params == "-h") {
                    NorsemenReference(videoOptions.keys.joinToString(","), ReferenceType.HELP)
                } else {
                    val videoReference = videoOptions.getValue(params)
                    NorsemenReference(videoReference, ReferenceType.VIDEO)
                }
            }

    companion object {
        private val videoOptions = mapOf(
                "attestup" to "https://www.youtube.com/watch?v=DwD7f5ZWhAk",
                "couples" to "https://www.youtube.com/watch?v=-luLqaEiszI",
                "cape" to "https://www.youtube.com/watch?v=A4uFJb9J9D4",
                "girl" to "https://www.youtube.com/watch?v=TXvweouf4oI",
                "funeral" to "https://www.youtube.com/watch?v=zHPZtmiwETM"
        )

        private val imageQuotes = mapOf(
                "concerns" to "https://i.redd.it/px7fymg0jow11.png"
        )

        private val textQuotes = arrayOf(
                "The sea giveth, and the sea taketh away. And this time the sea... tooketh... my sword.",
                "They say the first thing to go when you lose your hands is your fine motor skills.",
                "This... this is a thing I have decided to say now.",
                "I think I heard someone say 3-2-1 no one else can come.",
                "Keep your friends close and your enemies a little bit further away",
                "I gave you strict orders not to come creeping around the shitting log, ørm",
                "Was the operation a success? No, it wasn't a success at all.",
                "Actually, 9 out of 10 concerns are unfounded.",
                "Suck a fart out of my ass.",
                "Ok, lots of fine words."
        )
    }

}

data class NorsemenReference(
        val output: String,
        val type: ReferenceType
)

enum class ReferenceType {
    QUOTE,
    IMAGE,
    VIDEO,
    HELP
}