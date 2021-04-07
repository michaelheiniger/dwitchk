package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.CardForExchangeChosenUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameFacadeImpl @Inject constructor(
    private val gameCommunicator: GameCommunicator,
    private val cardForExchangeSubmitUsecase: CardForExchangeChosenUsecase,
    private val gameRepository: GameRepository,
    private val gameInteractor: GameInteractor,
    private val schedulerFactory: SchedulerFactory
) : GameFacade, GameCommunicator by gameCommunicator {

    override fun playCard(cardPlayed: Card): Completable {
        return gameInteractor.playCard(cardPlayed)
            .subscribeOn(schedulerFactory.io())
    }

    override fun pickCard(): Completable {
        return gameInteractor.pickCard()
            .subscribeOn(schedulerFactory.io())
    }

    override fun passTurn(): Completable {
        return gameInteractor.passTurn()
            .subscribeOn(schedulerFactory.io())
    }

    override fun startNewRound(): Completable {
        return gameInteractor.startNewRound()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeGameData(): Observable<DwitchState> {
        return gameRepository.observeGameData()
            .subscribeOn(schedulerFactory.io())
    }

    override fun submitCardsForExchange(cards: Set<Card>): Completable {
        return cardForExchangeSubmitUsecase.chooseCardForExchange(cards)
            .subscribeOn(schedulerFactory.io())
    }
}
