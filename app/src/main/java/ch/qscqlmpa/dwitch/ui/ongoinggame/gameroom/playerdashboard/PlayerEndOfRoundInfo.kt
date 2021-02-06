package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlayerEndOfRoundInfo(val name: String, val rankResource: Int) : Parcelable
