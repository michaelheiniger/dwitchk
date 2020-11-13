package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import javax.inject.Inject

class LaunchGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val serviceManager: ServiceManager,
    private val gameEventRepository: GuestGameEventRepository
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
        gameEventRepository.notify(GuestGameEvent.GameLaunched)
    }
}