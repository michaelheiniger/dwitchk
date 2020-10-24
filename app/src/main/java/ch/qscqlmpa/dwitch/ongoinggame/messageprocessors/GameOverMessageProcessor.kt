package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

internal class GameOverMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val gameEventRepository: GameEventRepository,
    private val communicator: GameCommunicator
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {
        return Maybe.fromCallable {
            val localPlayer = store.getLocalPlayer()

            if (localPlayer.playerRole == PlayerRole.HOST) {
                return@fromCallable MessageFactory.createGameOverMessage()
            } else {
                return@fromCallable null
            }
        }.flatMapCompletable { envelopeToSend -> communicator.sendMessage(envelopeToSend) }
            .doOnComplete { gameEventRepository.notifyOfEvent(GameEvent.GameOver) }
    }
}