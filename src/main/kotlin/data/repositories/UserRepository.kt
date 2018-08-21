package data.repositories

import com.timcastelijns.chatexchange.chat.User
import network.UserService
import java.time.Instant

class UserRepository(
        private val userService: UserService
) {

    suspend fun getUser(userId: Long): User? {
        val data = userService.getUser(userId.toString()).await()
        val userJson = data.get("users")
                .asJsonArray
                .map { it.asJsonObject }
                .firstOrNull() ?: return null

        return with(userJson) {
            val id = get("id").asLong
            val userName = get("name").asString
            val reputation = get("reputation").asInt
            val isModerator = if (get("is_moderator").isJsonNull) false else get("is_moderator").asBoolean
            val isRoomOwner = if (get("is_owner").isJsonNull) false else get("is_owner").asBoolean
            val lastSeen = if (get("last_seen").isJsonNull) null else Instant.ofEpochSecond(get("last_seen").asLong)
            val lastPost = if (get("last_post").isJsonNull) null else Instant.ofEpochSecond(get("last_post").asLong)
            val profileLink = "http://chat.stackoverflow.com/users/$id"

            User(id, userName, reputation, isModerator, isRoomOwner, lastSeen, lastPost,
                    false, profileLink)
        }

    }

}
