package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.UiInfoModel
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

internal class WaitingRoomHostViewModel @Inject
constructor(
    private val facade: WaitingRoomHostFacade,
    disposableManager: ch.qscqlmpa.dwitchcommonutil.DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomHostCommand>()

    fun canGameBeLaunched(): LiveData<UiControlModel> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.gameCanBeLaunched()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .map(::processGameLaunchableEvent)
                .doOnError { error -> Timber.e(error, "Error while observing if game can be launched.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun commands(): LiveData<WaitingRoomHostCommand> {
        return commands
    }

    fun launchGame() {
        disposableManager.add(facade.launchGame()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .subscribe(
                {
                    Timber.i("Game launched")
                    commands.value = WaitingRoomHostCommand.NavigateToGameRoomScreen
                },
                { error -> Timber.e(error, "Error while launching game") }
            )
        )
    }

    fun cancelGame() {
        disposableManager.add(
            facade.cancelGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    {
                        Timber.i("Game canceled")
                        commands.value = WaitingRoomHostCommand.NavigateToHomeScreen
                    },
                    { error -> Timber.e(error, "Error while canceling game") }
                )
        )
    }

    fun connectionStateInfo(): LiveData<UiInfoModel> {
        return LiveDataReactiveStreams.fromPublisher(currentCommunicationState()
            .map { state -> UiInfoModel(ResourceMapper.getResource(state)) })
    }

    private fun currentCommunicationState(): Flowable<HostCommunicationState> {
        return facade.observeCommunicationState()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .doOnError { error -> Timber.e(error, "Error while observing communication state.") }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    private fun processGameLaunchableEvent(event: GameLaunchableEvent): UiControlModel {
        return when (event) {
            GameLaunchableEvent.GameIsReadyToBeLaunched -> UiControlModel(enabled = true)
            GameLaunchableEvent.NotEnoughPlayers -> UiControlModel(enabled = false)
            GameLaunchableEvent.NotAllPlayersAreReady -> UiControlModel(enabled = false)
        }
    }
}
