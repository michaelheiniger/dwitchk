package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.usecases.CardForExchangeChosenUsecase
import ch.qscqlmpa.dwitchgame.ingame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@InGameScope
internal class GameInteractor @Inject constructor(
    private val store: InGameStore,
    private val gameUpdatedUsecase: GameUpdatedUsecase,
    private val dwitchFactory: DwitchFactory,
    private val cardDealerFactory: CardDealerFactory,
    private val cardForExchangeSubmitUsecase: CardForExchangeChosenUsecase
) {
    fun performAction(action: GameAction): Completable {
        return when (action) {
            GameAction.PassTurn -> handleGameStateUpdated { engine -> engine.passTurn() }
            is GameAction.PlayCard -> handleGameStateUpdated { engine -> engine.playCards(action.cardsPlayed) }
            GameAction.StartNewRound -> handleGameStateUpdated { engine -> engine.startNewRound(cardDealerFactory) }
            is GameAction.SubmitCardsForExchange -> cardForExchangeSubmitUsecase.chooseCardForExchange(action.cards)
        }
    }

    private fun handleGameStateUpdated(updateGameState: (engine: DwitchEngine) -> DwitchGameState): Completable {
        return Single.fromCallable { updateGameState(dwitchFactory.createDwitchEngine(store.getGameState())) }
            .flatMapCompletable(gameUpdatedUsecase::handleUpdatedGameState)
            .doOnError { error -> Logger.error(error) { "Error while updating the game state." } }
    }
}
