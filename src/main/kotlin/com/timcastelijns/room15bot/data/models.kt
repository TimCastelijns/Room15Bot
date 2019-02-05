package com.timcastelijns.room15bot.data

data class User(
        val id: Long,
        val name: String?
)

data class StarredMessage(
        val username: String,
        val message: String,
        val stars: Int,
        val permanentLink: String
)

data class UserStats(
        val reputation: Int,
        val nrQuestions: Int,
        val nrAnswers: Int,
        val formattedRatio: String
)

data class StarsData(
        val starredMessages: List<StarredMessage>,
        val totalStarredMessages: Int,
        val totalStars: Int
)
