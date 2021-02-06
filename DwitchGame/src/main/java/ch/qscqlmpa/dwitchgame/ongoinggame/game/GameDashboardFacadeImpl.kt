package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent.DwitchEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.CardForExchangeChosenUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameUpdatedUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.StartCardExchangeUsecase
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject

internal class GameDashboardFacadeImpl @Inject constructor(
    private val gameCommunicator: GameCommunicator,
    private val gameRepository: GameRepository,
    private val gameUpdatedUsecase: GameUpdatedUsecase,
    private val startCardExchangeUsecase: StartCardExchangeUsecase,
    private val cardForExchangeSubmitUsecase: CardForExchangeChosenUsecase,
    private val cardDealerFactory: CardDealerFactory,
    private val dwitchEventRepository: DwitchEventRepository,
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
        return handleGameStateUpdated { engine -> engine.startNewRound(cardDealerFactory) }
            .flatMapCompletable(startCardExchangeUsecase::startCardExchange)
    }

    override fun observeDashboardInfo(): Observable<GameDashboardInfo> {
        return Observable.combineLatest(
            gameRepository.observeGameInfo(),
            gameCommunicator.observePlayerConnectionState(),
            { gameInfo, localPlayerConnectionState ->
                GameDashboardInfo(
                    dwitchEngineFactory.create(gameInfo.gameState).getGameInfo(),
                    gameInfo.localPlayerId,
                    gameInfo.localPlayerIsHost,
                    localPlayerConnectionState
                )
            }
        ).subscribeOn(schedulerFactory.io())
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
            .doOnError { error -> Timber.e(error, "Error while updating the game state:") }
            .flatMap { updatedGameState ->
                gameUpdatedUsecase.handleUpdatedGameState(updatedGameState)
                    .andThen(Single.just(updatedGameState))
            }.subscribeOn(schedulerFactory.io())
    }
}
