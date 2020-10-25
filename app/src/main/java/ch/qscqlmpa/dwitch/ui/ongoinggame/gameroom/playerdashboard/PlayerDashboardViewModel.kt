package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractor
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class PlayerDashboardViewModel @Inject
constructor(private val gameInteractor: GameInteractor,
            private val gameEventRepository: GameEventRepository,
            disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<PlayerDashboardCommand>()

    fun commands(): LiveData<PlayerDashboardCommand> {
        val liveDataMerger = MediatorLiveData<PlayerDashboardCommand>()
        liveDataMerger.addSource(gameEventLiveData()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

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

    private fun gameEventLiveData(): LiveData<PlayerDashboardCommand> {
        return LiveDataReactiveStreams.fromPublisher(
            gameEventRepository.observeEvents()
                .observeOn(schedulerFactory.ui())
                .map(::getCommandForGameEvent)
                .doOnError { error -> Timber.e(error, "Error while observing game events.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun getCommandForGameEvent(event: GameEvent): PlayerDashboardCommand {
        return when (event) {
            GameEvent.GameCanceled -> TODO()
            GameEvent.GameLaunched -> TODO()
            GameEvent.GameOver -> PlayerDashboardCommand.NavigateToHomeScreen
        }
    }
}
