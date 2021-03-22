package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.utils.TextProvider
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardInfo
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class PlayerDashboardViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler,
    private val textProvider: TextProvider
) : BaseViewModel() {

    private val commands = MutableLiveData<PlayerDashboardCommand>()

    fun playerDashboard(): LiveData<GameDashboard> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeDashboardInfo()
                .map { dashboard -> GameDashboardFactory(dashboard, textProvider).create() }
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing player dashboard." } }
                .toFlowable(BackpressureStrategy.LATEST),
        )
    }

    fun commands(): LiveData<PlayerDashboardCommand> {
        val liveDataMerger = MediatorLiveData<PlayerDashboardCommand>()
        liveDataMerger.addSource(observeCardExchangeEvents()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(observeGamePhaseEvents()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun playCard(cardPlayed: Card) {
        performOperation("Card $cardPlayed played successfully.", "Error while playing card $cardPlayed.") {
            facade.playCard(
                cardPlayed
            )
        }
    }

    fun pickCard() {
        performOperation("Card picked successfully.", "Error while picking card.") { facade.pickCard() }
    }

    fun passTurn() {
        performOperation("Turn passed successfully.", "Error while passing turn.") { facade.passTurn() }
    }

    fun startNewRound() {
        performOperation("Start new round successfully.", "Error while starting new round.") { facade.startNewRound() }
    }

    private fun observeCardExchangeEvents(): LiveData<PlayerDashboardCommand.OpenCardExchange> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeCardExchangeEvents()
                .map { PlayerDashboardCommand.OpenCardExchange }
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing card exchange." } }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun observeGamePhaseEvents(): LiveData<PlayerDashboardCommand.OpenEndOfRound> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeDashboardInfo()
                .distinctUntilChanged { info -> info.gameInfo.gamePhase }
                .filter { info -> info.gameInfo.gamePhase == GamePhase.RoundIsOver }
                .map(::mapEndOfRoundInfo)
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing dashboard info." } }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun mapEndOfRoundInfo(info: GameDashboardInfo): PlayerDashboardCommand.OpenEndOfRound {
        return when (info.gameInfo.gamePhase) {
            GamePhase.RoundIsOver ->
                PlayerDashboardCommand.OpenEndOfRound(
                    info.gameInfo.playerInfos
                        .map { (_, p) -> PlayerEndOfRoundInfo(p.name, ResourceMapper.getResourceLong(p.rank)) }
                )
            else -> throw IllegalArgumentException("Illegal game phase: ${info.gameInfo.gamePhase}")
        }
    }

    private fun performOperation(successText: String, failureText: String, op: () -> Completable) {
        disposableManager.add(
            op()
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { successText } },
                    { error -> Logger.error(error) { failureText } }
                )
        )
    }
}
