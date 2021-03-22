package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo

data class ResumableGamesResponse(val status: Status, val resumableGames: List<ResumableGameInfo>, val error: Throwable?) {

    constructor(status: Status, resumableGames: List<ResumableGameInfo>) : this(status, resumableGames, null)

    companion object {

        fun success(games: List<ResumableGameInfo>): ResumableGamesResponse {
            return ResumableGamesResponse(
                Status.SUCCESS,
                games,
                null
            )
        }

        fun error(error: Throwable): ResumableGamesResponse {
            return ResumableGamesResponse(
                Status.ERROR,
                emptyList(),
                error
            )
        }
    }
}
