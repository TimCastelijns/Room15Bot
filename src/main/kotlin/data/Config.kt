package data

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
