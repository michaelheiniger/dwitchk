package ch.qscqlmpa.dwitch.ui.navigation

sealed class InGameDestination(override val routeName: String) : Destination(routeName) {
    object Loading : InGameDestination("loading")

    object InGameDispatch : InGameDestination("gameDispatch") {
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

    object WaitingRoomHost : InGameDestination("waitingRoomHost") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeDestination.Home,
                GameRoomHost -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object WaitingRoomGuest : InGameDestination("waitingRoomGuest") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeDestination.Home,
                GameRoomGuest -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object GameRoomHost : InGameDestination("gameRoomHost") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeDestination.Home -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }

    object GameRoomGuest : InGameDestination("gameRoomGuest") {
        override fun navigateTo(navigate: (nav: NavigationData) -> Unit, destination: Destination) {
            when (destination) {
                HomeDestination.Home -> navigate(NavigationData(destination))
                else -> super.navigateTo(navigate, destination)
            }
        }
    }
}
