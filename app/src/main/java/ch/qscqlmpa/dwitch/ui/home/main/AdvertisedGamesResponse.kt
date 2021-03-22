package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame

data class AdvertisedGamesResponse(val status: Status, val advertisedGames: List<AdvertisedGame>, val error: Throwable?) {

    constructor(status: Status, advertisedGames: List<AdvertisedGame>) : this(status, advertisedGames, null)

    companion object {

        fun success(games: List<AdvertisedGame>): AdvertisedGamesResponse {
            return AdvertisedGamesResponse(
                Status.SUCCESS,
                games,
                null
            )
        }

        fun error(error: Throwable): AdvertisedGamesResponse {
            return AdvertisedGamesResponse(
                Status.ERROR,
                emptyList(),
                error
            )
        }
    }
}
