package bot

import com.timcastelijns.chatexchange.chat.*
import data.commands.GetStarsOverviewCommand
import data.commands.GetUserStatsCommand
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import room.observeMessagesPosted
import room.observeUsersEntered

class Bot(
        private val getStarsOverviewCommand: GetStarsOverviewCommand,
        private val getUserStatsCommand: GetUserStatsCommand
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
        disposables.addAll(
                room.observeMessagesPosted()
                        .subscribe { processMessage(it) },
                room.observeUsersEntered()
                        .subscribe { processUserEntered(it) }
        )
    }

    private fun processUserEntered(user: User) {
        disposables.add(getUserStatsCommand.execute(user)
                .subscribe { it ->
                    room.send(it)
                })
    }

    private fun processMessage(message: Message) {
        when (message.plainContent) {
            "1" -> die(killer = message.user!!)
            "2" -> room.send("${message.user!!.id}")
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
