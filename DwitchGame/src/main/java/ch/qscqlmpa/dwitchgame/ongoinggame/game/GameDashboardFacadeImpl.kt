package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent.DwitchEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.CardForExchangeChosenUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameDashboardFacadeImpl @Inject constructor(
    private val gameRepository: GameRepository,
    private val dwitchEventRepository: DwitchEventRepository,
    private val gameCommunicator: GameCommunicator,
    private val gameUpdatedUsecase: GameUpdatedUsecase,
    private val cardForExchangeSubmitUsecase: CardForExchangeChosenUsecase,
    private val cardDealerFactory: CardDealerFactory,
    private val dwitchEngineFactory: DwitchEngineFactory,
    private val schedulerFactory: SchedulerFactory
) : GameDashboardFacade, GameCommunicator by gameCommunicator {

    override fun playCard(cardPlayed: Card): Completable {
        return handleGameStateUpdated { engine -> engine.playCard(cardPlayed) }.ignoreElement()
    }

    override fun pickCard(): Completable {
        return handleGameStateUpdated { engine -> engine.pickCard() }.ignoreElement()
    }

    override fun passTurn(): Completable {
        return handleGameStateUpdated { engine -> engine.passTurn() }.ignoreElement()
    }

    override fun startNewRound(): Completable {
        return handleGameStateUpdated { engine -> engine.startNewRound(cardDealerFactory) }.ignoreElement()
    }

    override fun observeDashboard(): Observable<GameDashboardInfo> {
        return Observable.combineLatest(
            gameRepository.observeGameInfo(),
            gameCommunicator.observePlayerConnectionState(),
            { gameInfo, localPlayerConnectionState ->
                GameDashboardFactory.createGameDashboardInfo(
                    dwitchEngineFactory.create(gameInfo.gameState).getGameInfo(),
                    gameInfo.localPlayerId,
                    localPlayerConnectionState
                )
            }
        ).subscribeOn(schedulerFactory.io())
    }

    override fun observeEndOfRound(): Observable<EndOfRoundInfo> {
        return gameRepository.observeGameInfo()
            .filter { gameInfo -> gameInfo.gameState.phase == GamePhase.RoundIsOver }
            .map { gameInfo ->
                val playerInfos = dwitchEngineFactory.create(gameInfo.gameState).getGameInfo().playerInfosList
                GameDashboardFactory.createEndOfGameInfo(playerInfos, gameInfo.localPlayerIsHost)
            }
            .subscribeOn(schedulerFactory.io())
    }

    //TODO: put logic somewhere else...
    override fun getEndOfRoundInfo(): Single<EndOfRoundInfo> {
        return gameRepository.getGameInfo()
            .map { gameInfo ->
                val playerInfos = dwitchEngineFactory.create(gameInfo.gameState).getGameInfo().playerInfos.values.toList()
                GameDashboardFactory.createEndOfGameInfo(
                    playerInfos.sortedWith { p1, p2 -> -p1.rank.value.compareTo(p2.rank.value) },
                    gameInfo.localPlayerIsHost
                )
            }
            .subscribeOn(schedulerFactory.io())
    }

    override fun submitCardsForExchange(cards: Set<Card>): Completable {
        return cardForExchangeSubmitUsecase.chooseCardForExchange(cards)
            .subscribeOn(schedulerFactory.io())
    }

    override fun getCardExchangeInfo(): Single<CardExchangeInfo> {
        return dwitchEventRepository.getCardExchangeInfo()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeCardExchangeEvents(): Observable<CardExchange> {
        return dwitchEventRepository.observeCardExchangeEvents()
            .subscribeOn(schedulerFactory.io())
    }

    private fun handleGameStateUpdated(updateGameState: (engine: DwitchEngine) -> GameState): Single<GameState> {
        return Single.fromCallable { updateGameState(dwitchEngineFactory.create(gameRepository.getGameState())) }
            .doOnError { error -> Logger.error(error) { "Error while updating the game state:" } }
            .flatMap { updatedGameState ->
                gameUpdatedUsecase.handleUpdatedGameState(updatedGameState)
                    .andThen(Single.just(updatedGameState))
            }.subscribeOn(schedulerFactory.io())
    }
}
