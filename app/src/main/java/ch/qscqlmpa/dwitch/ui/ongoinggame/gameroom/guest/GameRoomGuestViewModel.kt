package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import mu.KLogging
import javax.inject.Inject

internal class GameRoomGuestViewModel @Inject constructor(
    private val facade: GameRoomGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val commands = MutableLiveData<GameRoomGuestCommand>()

    fun commands(): LiveData<GameRoomGuestCommand> {
        val liveDataMerger = MediatorLiveData<GameRoomGuestCommand>()
        liveDataMerger.addSource(gameEventLiveData()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun acknowledgeGameOver() {
        commands.value = GameRoomGuestCommand.NavigateToHomeScreen
    }

    private fun gameEventLiveData(): LiveData<GameRoomGuestCommand> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeEvents()
                .observeOn(uiScheduler)
                .map(::getCommandForGameEvent)
                .doOnError { error -> logger.error(error) { "Error while observing game events." } }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun getCommandForGameEvent(event: GuestGameEvent): GameRoomGuestCommand {
        return when (event) {
            GuestGameEvent.GameOver -> GameRoomGuestCommand.ShowGameOverInfo
            else -> throw IllegalStateException("Event '$event' is not supposed to occur in GameRoom.")
        }
    }

    companion object : KLogging()
}
