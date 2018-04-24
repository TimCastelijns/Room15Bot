package room

import fr.tunaki.stackoverflow.chat.event.EventType
import fr.tunaki.stackoverflow.chat.Message
import fr.tunaki.stackoverflow.chat.Room
import io.reactivex.Observable

fun Room.observeMessagesPosted(): Observable<Message> {
    return Observable.create { emitter ->
        this.addEventListener(EventType.MESSAGE_POSTED) {
            emitter.onNext(it.message)
        }
    }
}
