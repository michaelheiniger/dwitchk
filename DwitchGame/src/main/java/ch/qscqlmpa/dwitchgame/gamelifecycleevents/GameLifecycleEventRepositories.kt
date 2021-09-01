package ch.qscqlmpa.dwitchgame.gamelifecycleevents

import android.os.Parcelable
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gameadvertising.GameCommonIdParceler
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import ch.qscqlmpa.dwitchstore.model.Game
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@GameScope
internal class GameStateRepository @Inject constructor(
    hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) {
    private var _state: GameState = GameState.NotStarted
    val state: GameState = _state

    init {
        hostGameLifecycleEventRepository.observeEvents().subscribe(
            { event ->
                _state = when (event) {
                    is HostGameLifecycleEvent.GameSetup,
                    HostGameLifecycleEvent.MovedToGameRoom -> GameState.Running
                    HostGameLifecycleEvent.GameOver -> GameState.Over
                }
            },
            { error -> Logger.error(error) { "Error while observing host game lifecycle events" } }
        )
        guestGameLifecycleEventRepository.observeEvents().subscribe(
            { event ->
                _state = when (event) {
                    is GuestGameLifecycleEvent.GameSetup,
                    GuestGameLifecycleEvent.GameJoined,
                    GuestGameLifecycleEvent.GameRejoined,
                    GuestGameLifecycleEvent.MovedToGameRoom -> GameState.Running
                    GuestGameLifecycleEvent.GameOver -> GameState.Over
                }
            },
            { error -> Logger.error(error) { "Error while observing host game lifecycle events" } }
        )
    }

    fun reset() {
        _state = GameState.NotStarted
    }
}

@GameScope
internal class HostGameLifecycleEventRepository @Inject constructor() : EventRepository<HostGameLifecycleEvent>()

@GameScope
internal class GuestGameLifecycleEventRepository @Inject constructor() : EventRepository<GuestGameLifecycleEvent>()

sealed class GameState {
    object NotStarted : GameState()
    object Running : GameState()
    object Over : GameState()
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

internal abstract class EventRepository<T> {
    private val eventRelay = PublishRelay.create<T>()

    fun observeEvents(): Observable<T> {
        Logger.debug { "Observing events..." }
        return eventRelay
    }

    fun notify(event: T) {
        Logger.debug { "Notify of event: $event" }
        eventRelay.accept(event)
    }
}

@Parcelize
data class GameCreatedInfo(
    val isNew: Boolean,
    val gameLocalId: Long,
    val gameCommonId: @WriteWith<GameCommonIdParceler>() GameCommonId,
    val gameName: String,
    val localPlayerLocalId: Long
) : Parcelable {

    constructor(insertGameResult: InsertGameResult) :
            this(
                isNew = true,
                insertGameResult.gameLocalId,
                insertGameResult.gameCommonId,
                insertGameResult.gameName,
                insertGameResult.localPlayerLocalId
            )
}

@Parcelize
data class GameJoinedInfo(
    val gameLocalId: Long,
    val localPlayerLocalId: Long,
    val gameIpAddress: String,
    val gamePort: Int
) : Parcelable {

    constructor(insertGameResult: InsertGameResult, advertisedGame: AdvertisedGame) :
            this(
                insertGameResult.gameLocalId,
                insertGameResult.localPlayerLocalId,
                advertisedGame.gameIpAddress,
                advertisedGame.gamePort
            )

    constructor(game: Game, advertisedGame: AdvertisedGame) : this(
        game.id,
        game.localPlayerLocalId,
        advertisedGame.gameIpAddress,
        advertisedGame.gamePort
    )
}
