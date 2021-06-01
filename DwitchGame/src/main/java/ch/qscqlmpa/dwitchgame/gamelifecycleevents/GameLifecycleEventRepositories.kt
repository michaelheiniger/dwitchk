package ch.qscqlmpa.dwitchgame.gamelifecycleevents

import android.os.Parcelable
import ch.qscqlmpa.dwitchgame.common.CachedEventRepository
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gameadvertising.GameCommonIdParceler
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import javax.inject.Inject

@GameScope
internal class HostGameLifecycleEventRepository @Inject constructor() : CachedEventRepository<HostGameLifecycleEvent>()

@GameScope
internal class GuestGameLifecycleEventRepository @Inject constructor() : CachedEventRepository<GuestGameLifecycleEvent>()

sealed class HostGameLifecycleEvent {
    data class GameCreated(val gameInfo: GameCreatedInfo) : HostGameLifecycleEvent()
    object MovedToGameRoom : HostGameLifecycleEvent()
    object GameOver : HostGameLifecycleEvent()
    object GameCanceled : HostGameLifecycleEvent()
}

sealed class GuestGameLifecycleEvent {
    data class GameJoined(val gameInfo: GameJoinedInfo) : GuestGameLifecycleEvent()
    object MovedToGameRoom : GuestGameLifecycleEvent()
    object KickedOffGame : GuestGameLifecycleEvent()
    object GuestLeftGame : GuestGameLifecycleEvent()
    object GameCanceled : GuestGameLifecycleEvent()
    object GameOver : GuestGameLifecycleEvent()
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
                true,
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
}
