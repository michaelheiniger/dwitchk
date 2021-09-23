package ch.qscqlmpa.dwitchcommunication.common

import android.os.Parcel
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import kotlinx.parcelize.Parceler
import java.util.*

object GameCommonIdParceler : Parceler<GameCommonId> {
    override fun create(parcel: Parcel) = GameCommonId(UUID.fromString(parcel.readString()!!))

    override fun GameCommonId.write(parcel: Parcel, flags: Int) {
        parcel.writeString(value.toString())
    }
}
