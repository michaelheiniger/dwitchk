package ch.qscqlmpa.dwitchmodel.game

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GameCommonId(val value: Long) : Parcelable
