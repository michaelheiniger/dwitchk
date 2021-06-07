package ch.qscqlmpa.dwitch.ui.home.home

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame

sealed class HomeDestination {
    data class JoinNewGame(val game: AdvertisedGame) : HomeDestination()
    object GameFragment : HomeDestination()
}
