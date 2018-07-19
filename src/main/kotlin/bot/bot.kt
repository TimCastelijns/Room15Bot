package bot

import com.timcastelijns.chatexchange.chat.ChatHost
import com.timcastelijns.chatexchange.chat.Room
import com.timcastelijns.chatexchange.chat.StackExchangeClient
import com.timcastelijns.chatexchange.chat.User
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class Bot {

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    lateinit var room: Room

    fun observeLife(): Observable<Boolean> {
        return aliveSubject.hide()
    }

    fun boot(client: StackExchangeClient, roomId: Int) {
        aliveSubject.onNext(true)
        joinRoom(client, roomId)
    }

    private fun die(killer: User) {
        aliveSubject.onNext(false)
        aliveSubject.onComplete()

        println("Died. Killed by ${killer.name}")
    }

    private fun joinRoom(client: StackExchangeClient, roomId: Int) {
        room = client.joinRoom(ChatHost.STACK_OVERFLOW, roomId)
        println("Joined room #$roomId")
    }

    fun start() {

    }
}
