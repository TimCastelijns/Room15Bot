package com.timcastelijns.room15bot.data

data class Credentials(
        val email: String,
        val password: String
)

data class DatabaseConfig(
        val user: String,
        val password: String,
        val url: String,
        val driver: String
)

data class BuildConfig(
        val version: String,
        val branch: String,
        val commit: String,
        val buildTime: String
)
