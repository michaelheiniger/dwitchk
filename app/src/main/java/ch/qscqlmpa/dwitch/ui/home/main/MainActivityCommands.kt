package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame

sealed class MainActivityCommands {
    data class NavigateToNewGameActivityAsGuest(val game: AdvertisedGame): MainActivityCommands()
    object NavigateToWaitingRoomAsGuest: MainActivityCommands()
    object NavigateToWaitingRoomAsHost: MainActivityCommands()
}