package util

import bot.commands.StarsData
import bot.commands.truncate

class MessageFormatter {

    fun asTableString(starsData: StarsData) = with(starsData) {
        if (starredMessages.isEmpty()) {
            return "No starred messages found"
        }

        val nameColumnMinLength = 6
        val nameColumnMaxLength = 10
        val messageColumnMaxLength = 48

        val longestNameLength = starredMessages.maxBy { it.username.length }!!.username.length
        val nameColumnLength = when {
            longestNameLength >= nameColumnMaxLength -> nameColumnMaxLength
            longestNameLength < nameColumnMinLength -> nameColumnMinLength
            else -> longestNameLength
        }

        val userHeader = "User".padEnd(nameColumnLength)
        val messageHeader = "Message ($totalStarredMessages)".padEnd(messageColumnMaxLength)
        val starsHeader = "Stars ($totalStars)"

        val header = " $userHeader | $messageHeader | $starsHeader | Link"
        val separator = "-".repeat(header.length)

        val table = mutableListOf<String>()
        table.add(header)
        table.add(separator)

        starredMessages.forEach {
            val user = it.username.truncate(nameColumnLength).padEnd(nameColumnLength)
            val message = it.message.sanitize().truncate(messageColumnMaxLength).padEnd(messageColumnMaxLength)
            val stars = it.stars.toString().truncate(starsHeader.length).padEnd(starsHeader.length)
            val permanentLink = ""
            val line = " $user | $message | $stars |$permanentLink"
            table.add(line)
        }

        table.joinToString("\n") { "    $it" }
    }

    private fun String.sanitize() = this.replace("\r", "").replace("\n", " ").trimEnd()

}
