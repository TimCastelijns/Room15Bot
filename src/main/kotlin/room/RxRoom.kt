package room

import com.timcastelijns.chatexchange.chat.Message
import com.timcastelijns.chatexchange.chat.Room
import com.timcastelijns.chatexchange.chat.User
import io.reactivex.Observable

fun Room.observeMessagesPosted(): Observable<Message> = Observable.create<Message> { emitter ->
    messagePostedEventListener = {
        emitter.onNext(it.message)
    }
}

fun Room.observeUsersEntered(): Observable<User> = Observable.create<User> { emitter ->
    userEnteredEventListener = {
        it.user?.let { emitter.onNext(it) }
    }
}
