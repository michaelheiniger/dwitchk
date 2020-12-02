package ch.qscqlmpa.dwitchmodel.gamediscovery

import android.os.Parcelable
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GameAdvertisingInfo(
    val gameCommonId: GameCommonId,
    val gameName: String,
    val gamePort: Int
): Parcelable