package ch.qscqlmpa.dwitchgame.gamelifecycle

import android.os.Parcelable
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.common.EventRepository
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchstore.InsertGameResult
import ch.qscqlmpa.dwitchstore.model.Game
import kotlinx.parcelize.Parcelize
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@GameScope
internal class GameStateRepository @Inject constructor(
    hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) {
    private var _lifecycleState: GameLifecycleState = GameLifecycleState.NotStarted
    val lifecycleState get(): GameLifecycleState = _lifecycleState

    init {
        hostGameLifecycleEventRepository.observeEvents().subscribe(
            { event ->
                _lifecycleState = when (event) {
                    is HostGameLifecycleEvent.GameSetup -> GameLifecycleState.RunningWaitingRoomHost
                    HostGameLifecycleEvent.MovedToGameRoom -> GameLifecycleState.RunningGameRoomHost
                    HostGameLifecycleEvent.GameOver -> GameLifecycleState.Over
                }
            },
            { error -> Logger.error(error) { "Error while observing host game lifecycle events" } }
        )
        guestGameLifecycleEventRepository.observeEvents().subscribe(
            { event ->
                _lifecycleState = when (event) {
                    is GuestGameLifecycleEvent.GameSetup,
                    GuestGameLifecycleEvent.GameJoined,
                    GuestGameLifecycleEvent.GameRejoined -> GameLifecycleState.RunningWaitingRoomGuest
                    GuestGameLifecycleEvent.MovedToGameRoom -> GameLifecycleState.RunningGameRoomGuest
                    GuestGameLifecycleEvent.GameOver -> GameLifecycleState.Over
                }
            },
            { error -> Logger.error(error) { "Error while observing host game lifecycle events" } }
        )
    }

    fun reset() {
        _lifecycleState = GameLifecycleState.NotStarted
    }
}

@GameScope
internal class HostGameLifecycleEventRepository @Inject constructor() : EventRepository<HostGameLifecycleEvent>()

@GameScope
internal class GuestGameLifecycleEventRepository @Inject constructor() : EventRepository<GuestGameLifecycleEvent>()

sealed class GameLifecycleState {
    object NotStarted : GameLifecycleState()
    object RunningWaitingRoomGuest : GameLifecycleState()
    object RunningGameRoomGuest : GameLifecycleState()
    object RunningWaitingRoomHost : GameLifecycleState()
    object RunningGameRoomHost : GameLifecycleState()
    object Over : GameLifecycleState()
}

sealed class HostGameLifecycleEvent {
    data class GameSetup(val gameInfo: GameCreatedInfo) : HostGameLifecycleEvent()
    object MovedToGameRoom : HostGameLifecycleEvent()
    object GameOver : HostGameLifecycleEvent()
}

sealed class GuestGameLifecycleEvent {
    data class GameSetup(val gameInfo: GameJoinedInfo) : GuestGameLifecycleEvent()
    object GameJoined : GuestGameLifecycleEvent()
    object GameRejoined : GuestGameLifecycleEvent()
    object MovedToGameRoom : GuestGameLifecycleEvent()
    object GameOver : GuestGameLifecycleEvent()
}

@Parcelize
data class GameCreatedInfo(
    val gameLocalId: Long,
    val localPlayerLocalId: Long
) : Parcelable {

    constructor(insertGameResult: InsertGameResult) :
        this(
            insertGameResult.gameLocalId,
            insertGameResult.localPlayerLocalId
        )
}

@Parcelize
data class GameJoinedInfo(
    val gameLocalId: Long,
    val localPlayerLocalId: Long,
    val advertisedGame: GameAdvertisingInfo
) : Parcelable {

    constructor(insertGameResult: InsertGameResult, advertisedGame: GameAdvertisingInfo) :
        this(
            insertGameResult.gameLocalId,
            insertGameResult.localPlayerLocalId,
            advertisedGame
        )

    constructor(game: Game, advertisedGame: GameAdvertisingInfo) : this(
        game.id,
        game.localPlayerLocalId,
        advertisedGame
    )
}
