package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

internal class GameInteractorImpl @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameUpdatedUsecase: GameUpdatedUsecase,
    private val cardDealerFactory: CardDealerFactory
) : GameInteractor {

    private val playerDashboardRelay = BehaviorRelay.create<PlayerDashboard>()

    override fun playCard(cardPlayed: Card): Completable {
        return handleGameStateUpdated { engine -> engine.playCard(cardPlayed) }
    }

    override fun pickCard(): Completable {
        return handleGameStateUpdated { engine -> engine.pickCard() }
    }

    override fun passTurn(): Completable {
        return handleGameStateUpdated { engine -> engine.passTurn() }
    }

    override fun startNewRound(): Completable {
        return handleGameStateUpdated { engine -> engine.startNewRound(cardDealerFactory) }
    }

    override fun observeDashboard(): Observable<PlayerDashboard> {
        return Observable.merge(
            playerDashboardRelay,
            gameRepository.observeGameInfo().map { gameInfo ->
                DwitchEngine(gameInfo.gameState).getPlayerDashboard(gameInfo.localPlayerId)
            }
        )
    }

    private fun handleGameStateUpdated(updateGameState: (engine: DwitchEngine) -> GameState): Completable {
        return Single.fromCallable {
            val gameState = gameRepository.getGameState()
            val updatedGameState = updateGameState(DwitchEngine(gameState))
            updateDashboard(updatedGameState)
            return@fromCallable updatedGameState
        }
            .doOnError { error -> Timber.e(error, "Error while updating the game state:") }
            .flatMapCompletable(gameUpdatedUsecase::handleUpdatedGameState)
    }

    private fun updateDashboard(gameState: GameState) {
        val playerId = gameRepository.getLocalPlayerId()
        playerDashboardRelay.accept(DwitchEngine(gameState).getPlayerDashboard(playerId))
    }
}