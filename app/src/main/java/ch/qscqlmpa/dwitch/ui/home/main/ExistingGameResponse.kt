package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo

data class ExistingGameResponse(val status: Status, val resumableGames: List<ResumableGameInfo>, val error: Throwable?) {

    companion object {

        fun success(games: List<ResumableGameInfo>): ExistingGameResponse {
            return ExistingGameResponse(
                Status.SUCCESS,
                games,
                null
            )
        }

        fun error(error: Throwable): ExistingGameResponse {
            return ExistingGameResponse(
                Status.ERROR,
                emptyList(),
                error
            )
        }
    }
}
