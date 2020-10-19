package ch.qscqlmpa.dwitch.ui.home.newgame

import java.util.*

class NewGameValidator {
    companion object {

        fun validate(gameName: String, playerName: String): List<NewGameError> {

            val errorList = ArrayList<NewGameError>()
            if (gameName.isEmpty()) {
                errorList.add(NewGameError.GAME_NAME_IS_EMPTY)
            }
            if (playerName.isEmpty()) {
                errorList.add(NewGameError.PLAYER_NAME_IS_EMPTY)
            }
            return errorList
        }
    }
}
