package ch.qscqlmpa.dwitch.ui.navigation

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo

sealed class HomeScreens(override val routeName: String) : Destination(routeName) {
    object Home : HomeScreens("home") {
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

    object HostNewGame : HomeScreens("hostNewGame") {
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

    data class JoinNewGame(val game: GameAdvertisingInfo) : HomeScreens(routeName) {
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

    object InGame : HomeScreens("inGame")
}
