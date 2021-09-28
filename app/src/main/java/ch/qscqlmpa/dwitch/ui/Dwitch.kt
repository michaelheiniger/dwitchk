package ch.qscqlmpa.dwitch.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.navigation
import ch.qscqlmpa.dwitch.ui.home.home.HomeScreen
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameScreen
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameScreen
import ch.qscqlmpa.dwitch.ui.ingame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.GameViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.host.GameRoomHostScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostScreen
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.tinylog.kotlin.Logger
import java.util.*

sealed class Destination(
    /**
     * Name of the route without any parameter.
     * Must NOT be used to actually navigate since the potential parameters aren't replaced with a concrete value.
     */
    open val routeName: String
) {

    /**
     * Value used to actually navigate to the destination. May contain parameters.
     */
    open fun toConcreteRoute(): String = routeName

    object Loading : Destination("loading")

    sealed class HomeScreens(override val routeName: String) : Destination(routeName) {
        object Home : HomeScreens("home")
        object HostNewGame : HomeScreens("hostNewGame")
        data class JoinNewGame(val gameCommonId: GameCommonId) : HomeScreens(routeName) {
            companion object {
                const val routeName = "joinNewGame/{gameCommonId}"
            }

            override fun toConcreteRoute(): String {
                return "joinNewGame/${gameCommonId.value}"
            }
        }

        object InGame : HomeScreens("inGame")
    }

    sealed class GameScreens(override val routeName: String) : Destination(routeName) {
        object Loading : GameScreens("loading")
        object GameDispatch : GameScreens("gameDispatch")
        object WaitingRoomHost : GameScreens("waitingRoomHost")
        object WaitingRoomGuest : GameScreens("waitingRoomGuest")
        object GameRoomHost : GameScreens("gameRoomHost")
        object GameRoomGuest : GameScreens("gameRoomGuest")
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Dwitch(
    vmFactory: ViewModelFactory,
    inGameVmFactory: () -> ViewModelFactory,
    navigationBridge: NavigationBridge
) {
    DwitchTheme {
        val navController = rememberNavController()
        Scaffold { innerPadding ->
            Navigation(navigationBridge, navController)
            DwitchNavHost(
                vmFactory = vmFactory,
                getInGameVmFactory = inGameVmFactory,
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DwitchNavHost(
    vmFactory: ViewModelFactory,
    getInGameVmFactory: () -> ViewModelFactory,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.HomeScreens.Home.routeName,
        modifier = modifier
    ) {
        composable(Destination.HomeScreens.Home.routeName) {
            HomeScreen(vmFactory = vmFactory)
        }
        composable(Destination.HomeScreens.HostNewGame.routeName) {
            HostNewGameScreen(vmFactory = vmFactory)
        }
        composable(route = Destination.HomeScreens.JoinNewGame.routeName) { entry ->
            val gameCommonId = GameCommonId(UUID.fromString(entry.arguments?.getString("gameCommonId")!!))
            JoinNewGameScreen(
                vmFactory = vmFactory,
                gameCommonId = gameCommonId
            )
        }

        navigation(
            route = Destination.HomeScreens.InGame.routeName,
            startDestination = Destination.GameScreens.GameDispatch.routeName
        ) {
            composable(Destination.GameScreens.GameDispatch.routeName) {
                val viewModel = viewModel<GameViewModel>(factory = getInGameVmFactory())
                DisposableEffect(viewModel) {
                    viewModel.onStart()
                    onDispose { viewModel.onStop() }
                }
                LoadingSpinner()
            }

            composable(Destination.GameScreens.WaitingRoomHost.routeName) {
                WaitingRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(Destination.GameScreens.WaitingRoomGuest.routeName) {
                WaitingRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
            composable(Destination.GameScreens.GameRoomHost.routeName) {
                GameRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(Destination.GameScreens.GameRoomGuest.routeName) {
                GameRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
        }
    }
}

@Suppress("LongMethod", "ComplexMethod", "NestedBlockDepth")
@Composable
private fun Navigation(
    navigationBridge: NavigationBridge,
    navController: NavHostController
) {
    when (val command = navigationBridge.command.value) {
        NavigationCommand.Identity -> {
            // Nothing to do
        }
        NavigationCommand.Back -> navController.popBackStack()
        is NavigationCommand.Navigate -> {
            val newDest = command.destination
            navController.currentDestination?.let { currentDest ->
                when (currentDest.route) {
                    Destination.HomeScreens.Home.routeName -> {
                        when (newDest) {
                            is Destination.HomeScreens.JoinNewGame,
                            Destination.HomeScreens.HostNewGame -> navController.navigate(newDest.toConcreteRoute())
                            Destination.HomeScreens.InGame -> {
                                navController.navigate(
                                    route = newDest.toConcreteRoute(),
                                    navOptions = navOptions {
                                        popUpTo(Destination.HomeScreens.Home.routeName) {
                                            inclusive = true
                                        }
                                    }
                                )
                            }
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.HomeScreens.HostNewGame.routeName,
                    Destination.HomeScreens.JoinNewGame.routeName -> {
                        when (newDest) {
                            Destination.HomeScreens.Home -> navController.navigate(newDest.toConcreteRoute())
                            Destination.HomeScreens.InGame -> {
                                navController.navigate(
                                    route = newDest.toConcreteRoute(),
                                    navOptions = navOptions {
                                        popUpTo(Destination.HomeScreens.Home.routeName) {
                                            inclusive = true
                                        }
                                    }
                                )
                            }
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.GameDispatch.routeName -> {
                        when (newDest) {
                            Destination.GameScreens.WaitingRoomGuest,
                            Destination.GameScreens.WaitingRoomHost,
                            Destination.GameScreens.GameRoomGuest,
                            Destination.GameScreens.GameRoomHost -> navController.navigate(newDest.toConcreteRoute())
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.WaitingRoomHost.routeName -> {
                        when (newDest) {
                            Destination.HomeScreens.Home,
                            Destination.GameScreens.GameRoomHost -> navController.navigate(newDest.toConcreteRoute())
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.WaitingRoomGuest.routeName -> {
                        when (newDest) {
                            Destination.HomeScreens.Home,
                            Destination.GameScreens.GameRoomGuest -> navController.navigate(newDest.toConcreteRoute())
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.GameRoomHost.routeName,
                    Destination.GameScreens.GameRoomGuest.routeName -> {
                        when (newDest) {
                            Destination.HomeScreens.Home -> navController.navigate(newDest.toConcreteRoute())
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    else -> Logger.warn { "Current route cannot be handled: ${currentDest.route}" }
                }
            }
        }
    }
}
