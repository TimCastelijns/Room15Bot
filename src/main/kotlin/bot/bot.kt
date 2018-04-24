package bot

import data.repositories.StarRepository
import fr.tunaki.stackoverflow.chat.ChatHost
import fr.tunaki.stackoverflow.chat.Message
import fr.tunaki.stackoverflow.chat.Room
import fr.tunaki.stackoverflow.chat.StackExchangeClient
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import room.observeMessagesPosted

class Bot(
        private val starRepository: StarRepository
) {

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    lateinit var room: Room

    private val disposables = CompositeDisposable()

    fun observeLife(): Observable<Boolean> {
        return aliveSubject.hide()
    }

    fun boot(client: StackExchangeClient, roomId: Int) {
        aliveSubject.onNext(true)
        joinRoom(client, roomId)
    }

    private fun die() {
        disposables.clear()

        aliveSubject.onNext(false)
        aliveSubject.onComplete()

        println("Died")
    }

    private fun joinRoom(client: StackExchangeClient, roomId: Int) {
        room = client.joinRoom(ChatHost.STACK_OVERFLOW, roomId)
        println("Joined room #$roomId")
    }

    fun start() {
        disposables.add(room.observeMessagesPosted()
                .subscribe { processMessage(it) })
    }

    private fun processMessage(message: Message) {
        when (message.plainContent) {
            "1" -> die()
            "2" -> room.send("${message.user.id}")
            "3" -> sync()
        }
    }

    private fun sync() {
        disposables.add(starRepository.getStarData()
                .subscribe({
                    println(it)
                }, {
                    println(it.message)
                }))
    }
}
