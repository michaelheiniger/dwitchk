package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable
import javax.inject.Inject

internal class CancelGameMessageProcessor @Inject constructor(private val store: InGameStore,
                                                              private val gameEventRepository: GameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {
        return Completable.fromCallable {
            store.deleteGame()
            gameEventRepository.notifyOfEvent(GameEvent.GameCanceled)
        }
    }
}