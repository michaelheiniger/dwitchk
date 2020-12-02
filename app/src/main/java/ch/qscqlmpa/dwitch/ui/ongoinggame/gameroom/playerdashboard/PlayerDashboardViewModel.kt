package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.utils.TextProvider
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
    disposableManager: ch.qscqlmpa.dwitchcommonutil.DisposableManager,
    schedulerFactory: SchedulerFactory,
    private val textProvider: TextProvider
) : BaseViewModel(disposableManager, schedulerFactory) {

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
