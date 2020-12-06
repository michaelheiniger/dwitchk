package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject

internal class PlayerDashboardFacadeImpl @Inject constructor(
    private val gameCommunicator: GameCommunicator,
    private val gameRepository: GameRepository,
    private val gameUpdatedUsecase: GameUpdatedUsecase,
    private val cardDealerFactory: CardDealerFactory
) : PlayerDashboardFacade {

    private val playerDashboardRelay = BehaviorRelay.create<PlayerDashboard>()

    override fun observeConnectionState(): Observable<PlayerConnectionState> {
        return gameCommunicator.observePlayerConnectionState()
    }

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
        return Single.fromCallable { updateGameState(DwitchEngine(gameRepository.getGameState())) }
            .doOnSuccess(::updateDashboard)
            .doOnError { error -> Timber.e(error, "Error while updating the game state:") }
            .flatMapCompletable(gameUpdatedUsecase::handleUpdatedGameState)
    }

    private fun updateDashboard(gameState: GameState) {
        val playerId = gameRepository.getLocalPlayerId()
        playerDashboardRelay.accept(DwitchEngine(gameState).getPlayerDashboard(playerId))
    }
}