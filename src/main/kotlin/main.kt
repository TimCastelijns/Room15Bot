import bot.Bot
import data.Credentials
import data.repositories.CredentialsRepository
import di.modules
import fr.tunaki.stackoverflow.chat.StackExchangeClient
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import java.util.concurrent.CountDownLatch

const val ROOM_ID_ANDROID = 15
const val ROOM_ID_TEST = 169617

fun main(args: Array<String>) {
    startKoin(modules)

    Application()
}

private fun connectClient(credentials: Credentials): StackExchangeClient {
    return StackExchangeClient(credentials.email, credentials.password)
}

class Application : KoinComponent {

    private val bot: Bot by inject()
    private val credentialsRepository: CredentialsRepository by inject()

    init {
        val countDownLatch = CountDownLatch(1)

        val credentials = credentialsRepository.getCredentials()
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
