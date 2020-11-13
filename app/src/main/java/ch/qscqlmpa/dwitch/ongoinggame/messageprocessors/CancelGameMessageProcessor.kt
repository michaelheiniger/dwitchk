package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import io.reactivex.Completable
import javax.inject.Inject

internal class CancelGameMessageProcessor @Inject constructor(private val store: InGameStore,
                                                              private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {
        return Completable.fromCallable {
            store.deleteGame()
            gameEventRepository.notify(GuestGameEvent.GameCanceled)
        }
    }
}