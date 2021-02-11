package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import javax.inject.Inject

internal class StartCardExchangeUsecase @Inject constructor(
    private val communicator: HostCommunicator,
    private val connectionStore: ConnectionStore,
    private val dwitchEngineFactory: DwitchEngineFactory
) {

    fun startCardExchange(gameState: GameState): Completable {
        return Completable.fromAction {
            dwitchEngineFactory.create(gameState).getCardsExchanges()
                .forEach { cardExchange ->
                    val playerId = cardExchange.playerId
                    val connectionId = connectionStore.getConnectionId(playerId)
                    if (connectionId != null) {
                        communicator.sendMessage(HostMessageFactory.createCardExchangeMessage(cardExchange, connectionId))
                    } else {
                        // TODO: handle case where the message cannot be send
                        logger.error { "No connection ID found in store for in-game ID: $playerId" }
                    }
                }
        }
    }

    companion object : KLogging()
}
