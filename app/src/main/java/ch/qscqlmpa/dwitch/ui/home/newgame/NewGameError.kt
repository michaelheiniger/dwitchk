package ch.qscqlmpa.dwitch.ui.home.newgame

import ch.qscqlmpa.dwitch.R

enum class NewGameError constructor(val ressourceId: Int) {
    SETUP_HOST_ERROR(R.string.setup_host_error),
    CONNECTION_TO_HOST_ERROR(R.string.connection_to_host_error),
    PLAYER_NAME_IS_EMPTY(R.string.nge_player_name_empty),
    GAME_NAME_IS_EMPTY(R.string.nge_game_name_empty)
}
