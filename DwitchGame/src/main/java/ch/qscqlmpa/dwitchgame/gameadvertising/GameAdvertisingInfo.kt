package ch.qscqlmpa.dwitchgame.gameadvertising

import android.os.Parcelable
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GameAdvertisingInfo(
    val isNew: Boolean,
    val gameCommonId: @WriteWith<GameCommonIdParceler>() GameCommonId,
    val gameName: String,
    val gamePort: Int
) : Parcelable
