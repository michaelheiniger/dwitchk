package ch.qscqlmpa.dwitch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import org.tinylog.kotlin.Logger

data class NavigationData(
    val destination: Destination,
    val navOptions: NavOptions?,
    val navigatorExtras: Navigator.Extras?
) {
    constructor(destination: Destination) : this(destination, null, null)
    constructor(destination: Destination, navOptions: NavOptions) : this(destination, navOptions, null)
}

fun navOptionsPopUpToInclusive(routeName: String): NavOptions {
    return navOptions {
        popUpTo(routeName) {
            inclusive = true
        }
    }
}

sealed class Destination(
    /**
     * Name of the route without any parameter.
     * Must NOT be used to actually navigate since the potential parameters aren't replaced with a concrete value.
     */
    open val routeName: String
) {

    /**
     * Value used to actually navigate to the destination. May contain parameters (actual values).
     */
    open fun toConcreteRoute(): String = routeName

    open fun navigateTo(
        navigate: (nav: NavigationData) -> Unit,
        destination: Destination
    ) {
        Logger.warn { "Route ${destination.routeName} cannot be reached from ${this.routeName}" }
    }
}

@Composable
fun HandleNavigation(
    navigationBridge: NavigationBridge,
    navController: NavHostController
) {
    fun navigate(navData: NavigationData) {
        navController.navigate(
            route = navData.destination.toConcreteRoute(),
            navOptions = navData.navOptions,
            navigatorExtras = navData.navigatorExtras
        )
    }

    fun getCurrentDestination(route: String): Destination {
        return when (route) {
            HomeDestination.Home.routeName -> HomeDestination.Home
            HomeDestination.HostNewGame.routeName -> HomeDestination.HostNewGame
            HomeDestination.JoinNewGame.routeName -> {
                val gameAd = navigationBridge.getData(route) as GameAdvertisingInfo
                HomeDestination.JoinNewGame(gameAd)
            }
            InGameDestination.InGameDispatch.routeName -> InGameDestination.InGameDispatch
            InGameDestination.WaitingRoomHost.routeName -> InGameDestination.WaitingRoomHost
            InGameDestination.WaitingRoomGuest.routeName -> InGameDestination.WaitingRoomGuest
            InGameDestination.GameRoomHost.routeName -> InGameDestination.GameRoomHost
            InGameDestination.GameRoomGuest.routeName -> InGameDestination.GameRoomGuest
            else -> throw IllegalArgumentException("Current route cannot be handled: $route")
        }
    }

    when (val command = navigationBridge.command.value) {
        NavigationCommand.Identity -> {
            // Nothing to do
        }
        NavigationCommand.Back -> navController.popBackStack()
        is NavigationCommand.Navigate -> {
            val destination = command.destination
            navController.currentDestination?.let { currentNavDestination ->
                val route = currentNavDestination.route
                if (route != null) {
                    getCurrentDestination(route).navigateTo(::navigate, destination)
                } else {
                    Logger.error { "No route for current NavDestination: $currentNavDestination" }
                }
            }
        }
    }
}
