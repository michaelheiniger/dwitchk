package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.ui.common.Status

data class AdvertisedGameResponse(val status: Status, val advertisedGames: List<AdvertisedGame>, val error: Throwable?) {

    companion object {

        fun success(games: List<AdvertisedGame>): AdvertisedGameResponse {
            return AdvertisedGameResponse(
                    Status.SUCCESS,
                    games,
                    null
            )
        }

        fun error(error: Throwable): AdvertisedGameResponse {
            return AdvertisedGameResponse(
                    Status.ERROR,
                    emptyList(),
                    error
            )
        }
    }
}
