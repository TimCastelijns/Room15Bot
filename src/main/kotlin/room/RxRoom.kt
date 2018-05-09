package room

import fr.tunaki.stackoverflow.chat.event.EventType
import fr.tunaki.stackoverflow.chat.Message
import fr.tunaki.stackoverflow.chat.Room
import fr.tunaki.stackoverflow.chat.User
import io.reactivex.Observable

fun Room.observeMessagesPosted(): Observable<Message> = Observable.create<Message> { emitter ->
    addEventListener(EventType.MESSAGE_POSTED) {
        emitter.onNext(it.message)
    }
}

fun Room.observeUsersEntered(): Observable<User> = Observable.create<User> { emitter ->
    addEventListener(EventType.USER_ENTERED) {
        it.user.ifPresent { emitter.onNext(it) }
    }
}
