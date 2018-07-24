import data.repositories.StarredMessage
import data.repositories.StarredMessageRepository
import io.reactivex.Single
import network.StarService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.test.assertEquals
import java.io.Reader



class StarredMessageRepositoryTest : KoinTest {

    private val module: Module = applicationContext {
        bean { StarredMessageRepository(MockStarService()) }
    }

    val repository: StarredMessageRepository by inject()

    @Before
    fun before() {
        startKoin(listOf(module))
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun test() {
        assertEquals<List<StarredMessage>>(
                listOf(StarredMessage("Mehdi B.", "", 10, "https://chat.stackoverflow.com/transcript/15?m=42215261#42215261")),
                repository.getStarredMessages().blockingGet()
        )
    }

}

class MockStarService : StarService {
    override fun getStarsData(): Single<String> {
        var html: String = ""
        FileInputStream("src/test/resources/page1.html").use {
            html = getFileContent(it)
        }
        return Single.just(html)
    }

    override fun getStarsDataByPage(page: Int): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getFileContent(fis: FileInputStream): String {
        val sb = StringBuilder()
        val r = InputStreamReader(fis, "UTF-8")  //or whatever encoding
        var ch = r.read()
        while (ch >= 0) {
            sb.append(ch)
            ch = r.read()
        }
        return sb.toString()
    }

}

