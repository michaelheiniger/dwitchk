package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import javax.inject.Inject

class LaunchGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val serviceManager: ServiceManager,
    private val gameEventRepository: GameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.LaunchGameMessage

        return storeGameState(msg)
            .doOnComplete { serviceManager.goToGuestGameRoom() }
            .doOnComplete { emitGameLaunchedEvent() }
    }

    private fun storeGameState(message: Message.LaunchGameMessage): Completable {
        return Completable.fromCallable { store.updateGameState(message.gameState) }
    }

    private fun emitGameLaunchedEvent() {
        gameEventRepository.notifyOfEvent(GameEvent.GameLaunched)
    }
}