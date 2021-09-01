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
import org.tinylog.kotlin.Logger

sealed class Destination(open val name: String) {
    object Loading : Destination("loading")

    sealed class HomeScreens(override val name: String) : Destination(name) {
        object Home : HomeScreens("home")
        object HostNewGame : HomeScreens("hostNewGame")
        data class JoinNewGame(val ipAddress: String) : HomeScreens("joinNewGame/$ipAddress")
        object InGame : HomeScreens("inGame")
    }

    sealed class GameScreens(override val name: String) : Destination(name) {
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
        startDestination = Destination.HomeScreens.Home.name,
        modifier = modifier
    ) {
        composable(Destination.HomeScreens.Home.name) {
            HomeScreen(vmFactory = vmFactory)
        }
        composable(Destination.HomeScreens.HostNewGame.name) {
            HostNewGameScreen(vmFactory = vmFactory)
        }
        composable(route = Destination.HomeScreens.JoinNewGame("{gameIpAddress}").name) { entry ->
            val gameIpAddress = entry.arguments?.getString("gameIpAddress")!!
            JoinNewGameScreen(
                vmFactory = vmFactory,
                gameIpAddress = gameIpAddress
            )
        }

        navigation(route = Destination.HomeScreens.InGame.name, startDestination = Destination.GameScreens.GameDispatch.name) {
            composable(Destination.GameScreens.GameDispatch.name) {
                val viewModel = viewModel<GameViewModel>(factory = getInGameVmFactory())
                DisposableEffect(viewModel) {
                    viewModel.onStart()
                    onDispose { viewModel.onStop() }
                }
                LoadingSpinner()
            }

            composable(Destination.GameScreens.WaitingRoomHost.name) {
                WaitingRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(Destination.GameScreens.WaitingRoomGuest.name) {
                WaitingRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
            composable(Destination.GameScreens.GameRoomHost.name) {
                GameRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(Destination.GameScreens.GameRoomGuest.name) {
                GameRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
        }
    }
}

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
                    Destination.HomeScreens.Home.name -> {
                        when (newDest) {
                            is Destination.HomeScreens.JoinNewGame,
                            Destination.HomeScreens.HostNewGame -> navController.navigate(newDest.name)
                            Destination.HomeScreens.InGame -> {
                                navController.navigate(
                                    route = newDest.name,
                                    navOptions = navOptions {
                                        popUpTo(Destination.HomeScreens.Home.name) {
                                            inclusive = true
                                        }
                                    }
                                )
                            }
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.HomeScreens.HostNewGame.name,
                    Destination.HomeScreens.JoinNewGame("{gameIpAddress}").name -> {
                        when (newDest) {
                            Destination.HomeScreens.Home -> navController.navigate(newDest.name)
                            Destination.HomeScreens.InGame -> {
                                navController.navigate(
                                    route = newDest.name,
                                    navOptions = navOptions {
                                        popUpTo(Destination.HomeScreens.Home.name) {
                                            inclusive = true
                                        }
                                    }
                                )
                            }
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.GameDispatch.name -> {
                        when (newDest) {
                            Destination.GameScreens.WaitingRoomGuest,
                            Destination.GameScreens.WaitingRoomHost,
                            Destination.GameScreens.GameRoomGuest,
                            Destination.GameScreens.GameRoomHost -> navController.navigate(newDest.name)
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.WaitingRoomHost.name -> {
                        when (newDest) {
                            Destination.HomeScreens.Home,
                            Destination.GameScreens.GameRoomHost -> navController.navigate(newDest.name)
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.WaitingRoomGuest.name -> {
                        when (newDest) {
                            Destination.HomeScreens.Home,
                            Destination.GameScreens.GameRoomGuest -> navController.navigate(newDest.name)
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    Destination.GameScreens.GameRoomHost.name,
                    Destination.GameScreens.GameRoomGuest.name -> {
                        when (newDest) {
                            Destination.HomeScreens.Home -> navController.navigate(newDest.name)
                            else -> Logger.warn { "Route $newDest cannot be reached from ${currentDest.route}" }
                        }
                    }
                    else -> Logger.warn { "Current route cannot be handled: ${currentDest.route}" }
                }
            }
        }
    }
}
