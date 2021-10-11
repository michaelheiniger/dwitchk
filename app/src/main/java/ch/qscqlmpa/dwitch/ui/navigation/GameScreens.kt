package ch.qscqlmpa.dwitch.ui.navigation

sealed class GameScreens(override val routeName: String) : Destination(routeName) {
    object Loading : GameScreens("loading")

    object GameDispatch : GameScreens("gameDispatch") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                WaitingRoomGuest,
                WaitingRoomHost,
                GameRoomGuest,
                GameRoomHost -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object WaitingRoomHost : GameScreens("waitingRoomHost") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeScreens.Home,
                GameRoomHost -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object WaitingRoomGuest : GameScreens("waitingRoomGuest") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeScreens.Home,
                GameRoomGuest -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object GameRoomHost : GameScreens("gameRoomHost") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeScreens.Home -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object GameRoomGuest : GameScreens("gameRoomGuest") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeScreens.Home -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }
}
