package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractor
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.utils.TextProvider
import ch.qscqlmpa.dwitch.utils.DisposableManager
import ch.qscqlmpa.dwitchengine.model.card.Card
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class PlayerDashboardViewModel @Inject constructor(
    private val gameInteractor: GameInteractor,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory,
    private val textProvider: TextProvider
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<PlayerDashboardCommand>()

    fun commands(): LiveData<PlayerDashboardCommand> {
        val liveDataMerger = MediatorLiveData<PlayerDashboardCommand>()
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun playerDashboard(): LiveData<PlayerDashboardUi> {
        return LiveDataReactiveStreams.fromPublisher(
            gameInteractor.observeDashboard()
                .map { dashboard -> PlayerDashboardUi(dashboard, textProvider) }
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .doOnError { error -> Timber.e(error, "Error while observing player dashboard.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun playCard(cardPlayed: Card) {
        performOperation(
            "Card $cardPlayed played successfully.",
            "Error while playing card $cardPlayed."
        )
        { gameInteractor.playCard(cardPlayed) }
    }

    fun pickCard() {
        performOperation("Card picked successfully.", "Error while picking card.")
        { gameInteractor.pickCard() }
    }

    fun passTurn() {
        performOperation("Turn passed successfully.", "Error while passing turn.")
        { gameInteractor.passTurn() }
    }

    fun startNewRound() {
        performOperation("Start new round successfully.", "Error while starting new round.")
        { gameInteractor.startNewRound() }
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
