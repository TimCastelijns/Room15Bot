package bot

import data.commands.GetStarsOverviewCommand
import fr.tunaki.stackoverflow.chat.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import room.observeMessagesPosted

class Bot (
        private val getStarsOverviewCommand: GetStarsOverviewCommand
){

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

    private fun die(killer: User) {
        disposables.clear()

        aliveSubject.onNext(false)
        aliveSubject.onComplete()

        println("Died. Killed by ${killer.name}")
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
            "1" -> die(killer = message.user)
            "2" -> room.send("${message.user.id}")
            "3" -> showStarsOverview()
        }
    }

    private fun showStarsOverview() {
        disposables.add(getStarsOverviewCommand.execute()
                .subscribe { it ->
                    room.send(it)
                })
    }
}
