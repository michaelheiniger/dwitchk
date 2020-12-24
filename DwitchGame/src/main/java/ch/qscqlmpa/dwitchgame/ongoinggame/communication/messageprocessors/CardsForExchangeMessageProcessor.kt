package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameRepository
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CardsForExchangeMessageProcessor @Inject constructor(
    private val gameRepository: GameRepository,
    communicatorLazy: Lazy<HostCommunicator>
) : BaseHostProcessor(communicatorLazy) {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.CardsForExchangeMessage

        return gameRepository.getGameEngineWithCurrentGameState()
            .map { engine -> engine.chooseCardsForExchange(msg.playerId, msg.cards) }
            .flatMapCompletable { updatedGameState ->
                Completable.merge(
                    listOf(
                        gameRepository.updateGameState(updatedGameState),
                        sendMessage(HostMessageFactory.createGameStateUpdatedMessage(updatedGameState))
                    )
                )
            }
    }
}