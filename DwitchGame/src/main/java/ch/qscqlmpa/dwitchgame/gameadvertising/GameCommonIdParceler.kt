package ch.qscqlmpa.dwitchgame.gameadvertising

import android.os.Parcel
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.parcelize.Parceler

object GameCommonIdParceler : Parceler<GameCommonId> {
    override fun create(parcel: Parcel) = GameCommonId(parcel.readLong())

    override fun GameCommonId.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(value)
    }
}
