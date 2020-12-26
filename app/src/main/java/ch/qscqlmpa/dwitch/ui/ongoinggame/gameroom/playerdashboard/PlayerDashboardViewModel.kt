package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.utils.TextProvider
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerDashboardFacade
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

class PlayerDashboardViewModel @Inject constructor(
    private val facade: PlayerDashboardFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory,
    private val textProvider: TextProvider
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<PlayerDashboardCommand>()

    fun playerDashboard(): LiveData<PlayerDashboardUi> {
        return LiveDataReactiveStreams.fromPublisher(
            Observable.combineLatest(
                facade.observeDashboard(),
                facade.observeConnectionState(),
                { dashboard, connectionState -> PlayerDashboardUi(dashboard, connectionState, textProvider) }
            ).subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .doOnError { error -> Timber.e(error, "Error while observing player dashboard.") }
                .toFlowable(BackpressureStrategy.LATEST),
        )
    }

    fun commands(): LiveData<PlayerDashboardCommand> {
        val liveDataMerger = MediatorLiveData<PlayerDashboardCommand>()
        liveDataMerger.addSource(observeCardExchangeEvents()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun playCard(cardPlayed: Card) {
        performOperation("Card $cardPlayed played successfully.", "Error while playing card $cardPlayed.")
        { facade.playCard(cardPlayed) }
    }

    fun pickCard() {
        performOperation("Card picked successfully.", "Error while picking card.")
        { facade.pickCard() }
    }

    fun passTurn() {
        performOperation("Turn passed successfully.", "Error while passing turn.")
        { facade.passTurn() }
    }

    fun startNewRound() {
        performOperation("Start new round successfully.", "Error while starting new round.")
        { facade.startNewRound() }
    }

    private fun observeCardExchangeEvents(): LiveData<PlayerDashboardCommand.OpenCardExchange> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeCardExchangeEvents()
                .map { PlayerDashboardCommand.OpenCardExchange }
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .doOnError { error -> Timber.e(error, "Error while observing card exchange.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun performOperation(successText: String, failureText: String, op: () -> Completable) {
        disposableManager.add(op()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .subscribe(
                { Timber.d(successText) },
                { error -> Timber.e(error, failureText) }
            ))
    }
}
