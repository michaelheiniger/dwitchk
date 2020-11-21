package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.Completable
import javax.inject.Inject

internal class LaunchGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val appEventRepository: AppEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.LaunchGameMessage

        return storeGameState(msg)
            .doOnComplete { appEventRepository.notify(AppEvent.GameRoomJoinedByGuest) }
            .doOnComplete { emitGameLaunchedEvent() }
    }

    private fun storeGameState(message: Message.LaunchGameMessage): Completable {
        return Completable.fromCallable { store.updateGameState(message.gameState) }
    }

    private fun emitGameLaunchedEvent() {
        gameEventRepository.notify(GuestGameEvent.GameLaunched)
    }
}