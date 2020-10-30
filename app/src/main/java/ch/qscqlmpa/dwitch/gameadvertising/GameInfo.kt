package ch.qscqlmpa.dwitch.gameadvertising

import android.os.Parcelable
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GameInfo(
    val gameCommonId: GameCommonId,
    val gameName: String,
    val gamePort: Int
): Parcelable