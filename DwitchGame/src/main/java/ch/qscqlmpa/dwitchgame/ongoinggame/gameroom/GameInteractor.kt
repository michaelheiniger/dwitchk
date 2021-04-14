package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@OngoingGameScope
internal class GameInteractor @Inject constructor(
    private val store: InGameStore,
    private val gameUpdatedUsecase: GameUpdatedUsecase,
    private val dwitchEngineFactory: DwitchEngineFactory,
    private val cardDealerFactory: CardDealerFactory,
) {
    fun playCard(cardPlayed: Card): Completable {
        return handleGameStateUpdated { engine -> engine.playCard(cardPlayed) }
    }

    fun passTurn(): Completable {
        return handleGameStateUpdated { engine -> engine.passTurn() }
    }

    fun startNewRound(): Completable {
        return handleGameStateUpdated { engine -> engine.startNewRound(cardDealerFactory) }
    }

    private fun handleGameStateUpdated(updateGameState: (engine: DwitchEngine) -> DwitchGameState): Completable {
        return Single.fromCallable { updateGameState(dwitchEngineFactory.create(store.getGameState())) }
            .flatMapCompletable(gameUpdatedUsecase::handleUpdatedGameState)
            .doOnError { error -> Logger.error(error) { "Error while updating the game state." } }
    }
}
