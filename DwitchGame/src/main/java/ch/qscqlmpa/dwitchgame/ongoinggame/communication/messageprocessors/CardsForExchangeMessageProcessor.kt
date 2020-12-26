package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.DwitchEngineFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class CardsForExchangeMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val dwitchEngineFactory: DwitchEngineFactory,
    communicatorLazy: Lazy<HostCommunicator>
) : BaseHostProcessor(communicatorLazy) {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.CardsForExchangeMessage

        return Single.fromCallable {
            val gameState = store.getGameState()
            val updatedGameState = dwitchEngineFactory.create(gameState).chooseCardsForExchange(msg.playerId, msg.cards)
            store.updateGameState(updatedGameState)
            return@fromCallable updatedGameState
        }
            .map(HostMessageFactory::createGameStateUpdatedMessage)
            .flatMapCompletable(this::sendMessage)
    }
}