package bot

import bot.monitors.ReminderMonitor
import bot.usecases.*
import com.timcastelijns.chatexchange.chat.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import util.CommandParser
import util.CommandType
import util.MessageFormatter

class Bot(
        private val getUserStatsCommand: GetUserStatsUseCase,
        private val syncStarsDataCommand: SyncStarsDataUseCase,
        private val getStarsDataCommand: GetStarsDataUseCase,
        private val setReminderCommand: SetReminderUseCase,
        private val reminderMonitor: ReminderMonitor,
        private val acceptUserUseCase: AcceptUserUseCase,
        private val rejectUserUseCase: RejectUserUseCase,
        private val messageFormatter: MessageFormatter
) {

    private val aliveSubject = BehaviorSubject.create<Boolean>()

    private lateinit var room: Room

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

        room.messageRepliedToEventListener = {
            println("${it.userName}: ${it.parentMessageId} <- ${it.message.content}")
            if (it.message.content!!.split(" ")[1].startsWith("!")) {
                processReply(it.message)
            }
        }

        monitorReminders()
    }

    private fun processUserRequestedAccess(user: User) {
        disposables.add(getUserStatsCommand.execute(user)
                .subscribe { it ->
                    room.send(messageFormatter.asRequestedAccessString(user, it))
                })
    }

    private fun processMessage(message: Message) {
        val rawCommand = message.content!!

        val command = try {
            CommandParser().parse(rawCommand)
        } catch (e: IllegalArgumentException) {
            processUnknownCommand(rawCommand)
            return
        }

        when (command.type) {
            CommandType.STATS_ME -> {
                val userId = message.user!!.id
                val user = room.getUser(userId)
                processShowStatsCommand(user)
            }
            CommandType.STATS_USER -> {
                val userId = command.args!!.toLong()
                val user = room.getUser(userId)
                processShowStatsCommand(user)
            }
            CommandType.STARS_ANY -> processShowStarsCommand(null)
            CommandType.STARS_USER -> {
                val username = command.args!!
                processShowStarsCommand(username)
            }
            CommandType.REMIND_ME -> {
                //TODO move processReply here
            }

            CommandType.ACCEPT -> processAcceptCommand(message.user!!, command.args!!)
            CommandType.REJECT-> processRejectCommand(message.user!!, command.args!!)
            CommandType.LEAVE -> processLeaveCommand(message.user!!)
            CommandType.SYNC_STARS -> processSyncStarsCommand(message.user!!)
        }
    }

    private fun processAcceptCommand(user: User, username: String) {
        if (!user.isRoomOwner) {
            return
        }

        val message = acceptUserUseCase.execute(username)
        room.send(message)
    }

    private fun processRejectCommand(user: User, username: String) {
        if (!user.isRoomOwner) {
            return
        }

        val message = rejectUserUseCase.execute(username)
        room.send(message)
    }

    private fun processLeaveCommand(user: User) {
        if (user.id == 1843331L) {
            room.send(messageFormatter.asLeavingString())
            die(killer = user)
        } else {
            room.send("\uD83D\uDD95\uD83C\uDFFB")
        }
    }

    private fun processReply(message: Message) {
        // TODO move to processMessage
        val command = message.content!!.substring(message.content!!.indexOf(" ") + 2)
        if (command.startsWith("remindme")) {
            processRemindMeCommand(message.id, command)
        }
    }

    private fun processShowStatsCommand(user: User) {
        disposables.add(getUserStatsCommand.execute(user)
                .subscribe { it ->
                    room.send(messageFormatter.asStatsString(user, it))
                })
    }

    private fun processSyncStarsCommand(user: User) {
        if (user.id != 1843331L) {
            messageFormatter.asNoAccessString()
            return
        }

        disposables.add(syncStarsDataCommand.execute(Unit)
                .subscribe {
                    room.send(messageFormatter.asDoneString())
                })
    }

    private fun processShowStarsCommand(username: String?) {
        disposables.add(getStarsDataCommand.execute(username)
                .subscribe { data ->
                    room.send(messageFormatter.asTableString(data))
                })
    }

    private fun processRemindMeCommand(messageId: Long, command: String) {
        val params = SetReminderCommandParams(messageId,
                command.substring("remindme".length + 1))

        disposables.add(setReminderCommand.execute(params)
                .observeOn(Schedulers.io())
                .subscribe({ triggerDate ->
                    room.send(messageFormatter.asReminderString(triggerDate))
                }, {
                    it.printStackTrace()
                    it.message?.let {
                        room.send(it)
                    }
                }))
    }

    private fun processUnknownCommand(rawCommand: String) {
        room.send(messageFormatter.asUnknownCommandString(rawCommand))
    }

    private fun monitorReminders() = disposables.add(reminderMonitor.start(room))

}
