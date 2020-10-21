package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractor
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class PlayerDashboardViewModel @Inject
constructor(private val gameInteractor: GameInteractor,
            disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    fun playerDashboard(): LiveData<PlayerDashboard> {
        return LiveDataReactiveStreams.fromPublisher(
                gameInteractor.observeDashboard()
                        .subscribeOn(schedulerFactory.io())
                        .observeOn(schedulerFactory.ui())
                        .doOnError { error -> Timber.e(error, "Error while observing player dashboard.") }
                        .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun playCard(cardPlayed: Card) {
        disposableManager.add(gameInteractor.playCard(cardPlayed)
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                        { Timber.d("Card $cardPlayed played successfully.") },
                        { error -> Timber.e(error, "Error while playing card $cardPlayed.") }
                ))
    }

    fun pickCard() {
        disposableManager.add(gameInteractor.pickCard()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                        { Timber.d("Card picked successfully.") },
                        { error -> Timber.e(error, "Error while picking card.") }
                ))
    }

    fun passTurn() {
        disposableManager.add(gameInteractor.passTurn()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                        { Timber.d("Turn passed successfully.") },
                        { error -> Timber.e(error, "Error while passing turn.") }
                ))
    }
}
