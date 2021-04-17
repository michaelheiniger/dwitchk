package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CardsForExchangeMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val dwitchEngineFactory: DwitchEngineFactory,
    communicatorLazy: Lazy<HostCommunicator>
) : BaseHostProcessor(communicatorLazy) {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.CardsForExchangeMessage

        return Completable.fromAction {
            val currentGameState = store.getGameState()
            val dwitchEngine = dwitchEngineFactory.create(currentGameState)

            /**
             * In case of
             * - duplicate message
             * - or when the host performs an exchange (since the update is already performed locally)
             * then we don't need to update the game state again
             */
            if (dwitchEngine.getCardExchangeIfRequired(msg.playerId) != null) {
                val updatedGameState = dwitchEngine.chooseCardsForExchange(msg.playerId, msg.cards)
                store.updateGameState(updatedGameState)
                sendMessage(HostMessageFactory.createGameStateUpdatedMessage(updatedGameState))
            } else {
                sendMessage(HostMessageFactory.createGameStateUpdatedMessage(currentGameState))
            }
        }
    }
}
