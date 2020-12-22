package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

class StartCardExchangeUsecase @Inject constructor(
    private val communicator: HostCommunicator,
    private val connectionStore: ConnectionStore
) {

    fun startCardExchange(gameState: GameState): Completable {
        return getCardExchanges(gameState)
            .flatMapCompletable { (inGameId, cardExchange) ->
                val connectionId = connectionStore.getConnectionId(inGameId)
                if (connectionId != null) {
                    communicator.sendMessage(HostMessageFactory.createCardExchangeMessage(inGameId, cardExchange, connectionId))
                } else {
                    Timber.e("No connection ID found in store for inGameId $inGameId") //TODO: handle case where the message cannot be send
                    Completable.complete()
                }
            }
    }

    private fun getCardExchanges(gameState: GameState): Observable<Pair<PlayerInGameId, CardExchange>> {
        return Observable.fromIterable(DwitchEngine(gameState).getCardsExchanges())
    }
}