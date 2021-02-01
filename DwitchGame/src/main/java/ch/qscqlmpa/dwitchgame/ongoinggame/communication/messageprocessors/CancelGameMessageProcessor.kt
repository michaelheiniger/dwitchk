package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CancelGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.fromCallable {
            if (store.gameIsNew()) {
                store.deleteGame()
            }
            gameEventRepository.notify(GuestGameEvent.GameCanceled)
        }
    }
}