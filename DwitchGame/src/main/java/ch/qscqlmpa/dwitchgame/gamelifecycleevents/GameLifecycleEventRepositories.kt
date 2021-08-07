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
internal class HostGameLifecycleEventRepository @Inject constructor() : EventRepository<HostGameLifecycleEvent>() {
    private var _gameRunning: Boolean = false

    val gameRunning: Boolean get() = _gameRunning

    override fun notify(event: HostGameLifecycleEvent) {
        super.notify(event)
        _gameRunning = when (event) {
            is HostGameLifecycleEvent.GameCreated, HostGameLifecycleEvent.MovedToGameRoom -> true
            HostGameLifecycleEvent.GameOver -> false
        }
    }
}

@GameScope
internal class GuestGameLifecycleEventRepository @Inject constructor() : EventRepository<GuestGameLifecycleEvent>() {
    private var _gameRunning: Boolean = false

    val gameRunning: Boolean get() = _gameRunning

    override fun notify(event: GuestGameLifecycleEvent) {
        super.notify(event)
        _gameRunning = when (event) {
            is GuestGameLifecycleEvent.GameJoined, GuestGameLifecycleEvent.MovedToGameRoom -> true
            GuestGameLifecycleEvent.GameOver -> false
        }
    }
}

sealed class HostGameLifecycleEvent {
    data class GameCreated(val gameInfo: GameCreatedInfo) : HostGameLifecycleEvent()
    object MovedToGameRoom : HostGameLifecycleEvent()
    object GameOver : HostGameLifecycleEvent()
}

sealed class GuestGameLifecycleEvent {
    data class GameJoined(val gameInfo: GameJoinedInfo) : GuestGameLifecycleEvent()
    object MovedToGameRoom : GuestGameLifecycleEvent()
    object GameOver : GuestGameLifecycleEvent()
}

internal abstract class EventRepository<T> {
    private val eventRelay = PublishRelay.create<T>()

    fun observeEvents(): Observable<T> {
        Logger.debug { "Observing events..." }
        return eventRelay
    }

    open fun notify(event: T) {
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
    val localPlayerLocalId: Long,
    val gamePort: Int
) : Parcelable {

    constructor(insertGameResult: InsertGameResult, gamePort: Int) :
            this(
                isNew = true,
                insertGameResult.gameLocalId,
                insertGameResult.gameCommonId,
                insertGameResult.gameName,
                insertGameResult.localPlayerLocalId,
                gamePort
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
