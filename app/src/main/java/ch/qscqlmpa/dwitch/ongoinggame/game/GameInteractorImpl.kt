package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
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

    override fun endGame(): Completable {
        TODO("Not yet implemented")
    }

    override fun observeDashboard(): Observable<PlayerDashboard> {
        return Observable.merge(
                playerDashboardRelay,
                gameRepository.observeGameInfo().map { gameInfo -> DwitchEngine(gameInfo).getPlayerDashboard() }
        )
    }

    private fun updateDashboard(gameInfo: GameInfo) {
        playerDashboardRelay.accept(DwitchEngine(gameInfo).getPlayerDashboard())
    }

    private fun handleGameStateUpdated(updateGameState: (engine: DwitchEngine) -> GameInfo): Completable {
        return gameRepository.getGameInfo()
                .map { gameInfo -> updateGameState(DwitchEngine(gameInfo)) }
                .doOnSuccess(this::updateDashboard)
                .doOnError { error -> Timber.e(error, "Error while updating the game state:") }
                .flatMapCompletable(gameUpdatedUsecase::handleUpdatedGameState)
    }
}