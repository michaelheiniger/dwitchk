package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.DwitchEngineFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

internal class StartCardExchangeUsecase @Inject constructor(
    private val communicator: HostCommunicator,
    private val connectionStore: ConnectionStore,
    private val dwitchEngineFactory: DwitchEngineFactory
) {

    fun startCardExchange(gameState: GameState): Completable {
        return getCardExchanges(gameState)
            .flatMapCompletable { cardExchange ->
                val playerId = cardExchange.playerId
                val connectionId = connectionStore.getConnectionId(playerId)
                if (connectionId != null) {
                    communicator.sendMessage(HostMessageFactory.createCardExchangeMessage(cardExchange, connectionId))
                } else {
                    //TODO: handle case where the message cannot be send
                    Timber.e("No connection ID found in store for in-game ID: $playerId")
                    Completable.complete()
                }
            }
    }

    private fun getCardExchanges(gameState: GameState): Observable<CardExchange> {
        return Observable.fromIterable(dwitchEngineFactory.create(gameState).getCardsExchanges())
    }
}