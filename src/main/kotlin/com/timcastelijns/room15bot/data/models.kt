package com.timcastelijns.room15bot.data

data class User(
        val id: Long,
        val name: String?,
        val profileId: Int?
)

data class UserProfile(
        val id: Int,
        val nickname: String?,
        val age: Int?
)

data class StarredMessage(
        val username: String,
        val message: String,
        val stars: Int,
        val permanentLink: String,
        val age: Int
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
