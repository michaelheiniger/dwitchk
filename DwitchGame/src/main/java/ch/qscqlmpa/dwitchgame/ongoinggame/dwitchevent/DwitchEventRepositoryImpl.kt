package ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent

import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class DwitchEventRepositoryImpl @Inject constructor(
    private val store: InGameStore,
    private val dwitchEngineFactory: DwitchEngineFactory
) : DwitchEventRepository {

    override fun getCardExchangeInfo(): Single<CardExchangeInfo> {
        return Single.fromCallable {
            val gameState = store.getGameState()
            val localPlayerId = store.getLocalPlayerDwitchId()
            val cardExchange = dwitchEngineFactory.create(gameState).getCardsExchange(localPlayerId)
                ?: throw IllegalStateException("No card exchange to perform.")
            CardExchangeInfo(cardExchange, gameState.player(localPlayerId).cardsInHand)
        }
    }

    override fun observeCardExchangeEvents(): Observable<CardExchange> {
        return store.observeGameState()
            .flatMap { gameState ->
                if (gameState.phase == GamePhase.RoundIsBeginningWithCardExchange) {
                    val cardExchange = dwitchEngineFactory.create(gameState).getCardsExchange(store.getLocalPlayerDwitchId())
                    if (cardExchange != null) {
                        return@flatMap Observable.just(cardExchange)
                    }
                    return@flatMap Observable.empty()
                }
                return@flatMap Observable.empty()
            }
    }
}
