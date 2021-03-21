package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo

data class ResumableGameResponse(val status: Status, val resumableGames: List<ResumableGameInfo>, val error: Throwable?) {

    constructor(status: Status, resumableGames: List<ResumableGameInfo>) : this(status, resumableGames, null)

    companion object {

        fun success(games: List<ResumableGameInfo>): ResumableGameResponse {
            return ResumableGameResponse(
                Status.SUCCESS,
                games,
                null
            )
        }

        fun error(error: Throwable): ResumableGameResponse {
            return ResumableGameResponse(
                Status.ERROR,
                emptyList(),
                error
            )
        }
    }
}
