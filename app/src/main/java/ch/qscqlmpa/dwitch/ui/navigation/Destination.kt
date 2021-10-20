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

    object InGameGuest : HomeDestination("inGameGuest")
    object InGameHost : HomeDestination("inGameHost")
}

sealed class InGameGuestDestination(name: String) : Destination("${prefix}$name") {
    companion object {
        private const val prefix = "inGameGuest"
    }

    object Home : InGameGuestDestination("Loading")
    object WaitingRoom : InGameGuestDestination("WaitingRoom")
    object GameRoom : InGameGuestDestination("GameRoom")
}

sealed class InGameHostDestination(name: String) : Destination("${prefix}$name") {
    companion object {
        private const val prefix = "inGameHost"
    }

    object Home : InGameHostDestination("Loading")
    object WaitingRoom : InGameHostDestination("WaitingRoom")
    object GameRoom : InGameHostDestination("GameRoom")
}
