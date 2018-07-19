package bot

import com.timcastelijns.chatexchange.chat.*
import data.commands.GetUserStatsCommand
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class Bot(
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
        room.accessLevelChangedEventListener = {
            if (it.accessLevel == AccessLevel.REQUEST) {
                processUserRequestedAccess(it.targetUser)
            }
        }

        room.messagePostedEventListener = {
            println("${it.userName}: ${it.message.content}")

            if (it.message.content!!.startsWith("!")) {
                processMessage(it.message)
            }
        }
    }

    private fun processUserRequestedAccess(user: User) {
        disposables.add(getUserStatsCommand.execute(user)
                .subscribe { it ->
                    room.send("${user.name} requested access. $it")
                })
    }

    private fun processMessage(message: Message) {
        val command = message.content!!.substring(1)
        if (command == "shoo" && message.user!!.id == 1843331L) {
            die(killer = message.user!!)
        } else if (command.startsWith("stats")) {
            val userId = command.split(" ").last().toLongOrNull()

            val user = if (userId == null) {
                message.user!!
            } else {
                room.getUser(userId)
            }

            processShowStatsCommand(user)
        }
    }

    private fun processShowStatsCommand(user: User) {
        disposables.add(getUserStatsCommand.execute(user)
                .subscribe { it ->
                    room.send("Stats for ${user.name} -- $it")
                })
    }
}
