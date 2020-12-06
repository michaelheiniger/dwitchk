package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchgame.ongoinggame.DwitchEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.StartCardExchangeUsecase
import ch.qscqlmpa.dwitchmodel.game.DwitchEvent
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
    private val startCardExchangeUsecase: StartCardExchangeUsecase,
    private val cardForExchangeChosenUsecase: CardForExchangeChosenUsecase,
    private val cardDealerFactory: CardDealerFactory,
    private val dwitchEventRepository: DwitchEventRepository
) : PlayerDashboardFacade {

    private val playerDashboardRelay = BehaviorRelay.create<PlayerDashboard>()

    override fun playCard(cardPlayed: Card): Completable {
        return handleGameStateUpdated { engine -> engine.playCard(cardPlayed) }
            .ignoreElement()
    }

    override fun pickCard(): Completable {
        return handleGameStateUpdated { engine -> engine.pickCard() }
            .ignoreElement()
    }

    override fun passTurn(): Completable {
        return handleGameStateUpdated { engine -> engine.passTurn() }
            .ignoreElement()
    }

    override fun startNewRound(): Completable {
        return handleGameStateUpdated { engine -> engine.startNewRound(cardDealerFactory) }
            .flatMapCompletable(startCardExchangeUsecase::startCardExchange)
    }

    override fun observeConnectionState(): Observable<PlayerConnectionState> {
        return gameCommunicator.observePlayerConnectionState()
    }

    override fun observeDashboard(): Observable<PlayerDashboard> {
        return Observable.merge(
            playerDashboardRelay,
            gameRepository.observeGameInfo().map { gameInfo ->
                DwitchEngine(gameInfo.gameState).getPlayerDashboard(gameInfo.localPlayerId)
            }
        )
    }

    override fun getDashboard(): Single<PlayerDashboard> {
        return gameRepository.getGameInfo().map { gameInfo ->
            DwitchEngine(gameInfo.gameState).getPlayerDashboard(gameInfo.localPlayerId)
        }
    }

    override fun observeCardExchangeEvents(): Observable<DwitchEvent.CardExchange> {
        return dwitchEventRepository.observeCardExchangeEvents()
    }

    override fun cardForExchangeChosen() {

    }

    private fun handleGameStateUpdated(updateGameState: (engine: DwitchEngine) -> GameState): Single<GameState> {
        return Single.fromCallable {
            val gameState = gameRepository.getGameState()
            val updatedGameState = updateGameState(DwitchEngine(gameState))
            updateDashboard(updatedGameState)
            return@fromCallable updatedGameState
        }
            .doOnError { error -> Timber.e(error, "Error while updating the game state:") }
            .flatMap { updatedGameState ->
                gameUpdatedUsecase.handleUpdatedGameState(updatedGameState)
                    .andThen(Single.just(updatedGameState))
            }
    }

    private fun updateDashboard(gameState: GameState) {
        val playerId = gameRepository.getLocalPlayerId()
        playerDashboardRelay.accept(DwitchEngine(gameState).getPlayerDashboard(playerId))
    }
}