package ch.qscqlmpa.dwitch.ui.navigation

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo

sealed class HomeDestination(override val routeName: String) : Destination(routeName) {
    object Home : HomeDestination("home") {
        override fun navigateTo(
            navigate: (nav: NavigationData) -> Unit,
            destination: Destination
        ) {
            when (destination) {
                is JoinNewGame,
                HostNewGame -> navigate(NavigationData(destination))
                InGame -> {
                    navigate(
                        NavigationData(
                            destination,
                            navOptionsPopUpToInclusive(routeName)
                        )
                    )
                }
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object HostNewGame : HomeDestination("hostNewGame") {
        override fun navigateTo(
            navigate: (nav: NavigationData) -> Unit,
            destination: Destination
        ) {
            when (destination) {
                Home -> navigate(NavigationData(destination))
                InGame -> {
                    navigate(
                        NavigationData(
                            destination,
                            navOptionsPopUpToInclusive(routeName)
                        )
                    )
                }
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    data class JoinNewGame(val game: GameAdvertisingInfo) : HomeDestination(routeName) {
        companion object {
            const val routeName = "joinNewGame"
        }

        override fun navigateTo(
            navigate: (nav: NavigationData) -> Unit,
            destination: Destination
        ) {
            when (destination) {
                Home -> navigate(NavigationData(destination))
                InGame -> {
                    navigate(
                        NavigationData(
                            destination,
                            navOptionsPopUpToInclusive(routeName)
                        )
                    )
                }
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object InGame : HomeDestination("inGame")
}
