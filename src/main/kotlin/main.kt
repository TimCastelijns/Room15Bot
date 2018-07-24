import bot.Bot
import com.timcastelijns.chatexchange.chat.StackExchangeClient
import data.Credentials
import data.db.Database
import data.repositories.ConfigRepository
import di.modules
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import java.util.concurrent.CountDownLatch

const val ROOM_ID_ANDROID = 15
const val ROOM_ID_TEST = 1

fun main(args: Array<String>) {
    startKoin(modules)

    Application()
}

private fun connectClient(credentials: Credentials): StackExchangeClient {
    return StackExchangeClient(credentials.email, credentials.password)
}

class Application : KoinComponent {

    private val configRepository: ConfigRepository by inject()
    private val database : Database by inject()
    private val bot: Bot by inject()

    init {
        database.connect()
        val countDownLatch = CountDownLatch(1)

        val credentials = configRepository.getBotCredentials()
        val client = connectClient(credentials)

        bot.boot(client, ROOM_ID_TEST)
        bot.observeLife()
                .subscribe { alive ->
                    if (!alive) {
                        countDownLatch.countDown()
                    }
                }

        bot.start()

        client.use { _ ->
            countDownLatch.await()
        }
    }
}
