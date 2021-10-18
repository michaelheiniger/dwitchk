package ch.qscqlmpa.dwitch.ui.navigation

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo

sealed class Destination(
    /**
     * Name of the route without any parameter.
     * Must NOT be used to actually navigate since the potential parameters aren't replaced with a concrete value.
     */
    open val routeName: String
) {
    open fun dataToSave(): Map<String, Any> {
        return emptyMap()
    }
}

sealed class HomeDestination(override val routeName: String) : Destination(routeName) {
    object Home : HomeDestination("home")
    object HostNewGame : HomeDestination("hostNewGame")
    data class JoinNewGame(val game: GameAdvertisingInfo) : HomeDestination(routeName) {
        companion object {
            const val routeName = "joinNewGame"
            const val gameParamName = "game"
        }

        override fun dataToSave(): Map<String, Any> {
            return mapOf(gameParamName to game)
        }
    }

    object InGame : HomeDestination("inGame")
}

sealed class InGameDestination(override val routeName: String) : Destination(routeName) {
    object Loading : InGameDestination("loading")
    object InGameDispatch : InGameDestination("gameDispatch")
    object WaitingRoomHost : InGameDestination("waitingRoomHost")
    object WaitingRoomGuest : InGameDestination("waitingRoomGuest")
    object GameRoomHost : InGameDestination("gameRoomHost")
    object GameRoomGuest : InGameDestination("gameRoomGuest")
}