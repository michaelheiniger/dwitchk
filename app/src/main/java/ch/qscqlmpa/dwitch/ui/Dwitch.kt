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
import ch.qscqlmpa.dwitch.ui.navigation.HandleNavigation
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameDestination
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import org.tinylog.kotlin.Logger

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Dwitch(
    vmFactory: ViewModelFactory,
    inGameVmFactory: () -> ViewModelFactory,
    navigationBridge: NavigationBridge
) {
    DwitchTheme {
        val navController = rememberNavController()
        HandleNavigation(navigationBridge, navController)
        Scaffold { innerPadding ->
            DwitchNavHost(
                navigationBridge = navigationBridge,
                vmFactory = vmFactory,
                getInGameVmFactory = inGameVmFactory,
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DwitchNavHost(
    navigationBridge: NavigationBridge,
    vmFactory: ViewModelFactory,
    getInGameVmFactory: () -> ViewModelFactory,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.Home.routeName,
        modifier = modifier
    ) {
        composable(HomeDestination.Home.routeName) {
            HomeScreen(vmFactory = vmFactory)
        }
        composable(HomeDestination.HostNewGame.routeName) {
            HostNewGameScreen(vmFactory = vmFactory)
        }
        composable(route = HomeDestination.JoinNewGame.routeName) { entry ->
            val gameAd = navigationBridge.getData(entry.destination.route!!) as GameAdvertisingInfo?
            if (gameAd != null) {
                JoinNewGameScreen(
                    vmFactory = vmFactory,
                    gameAd = gameAd
                )
            } else {
                Logger.warn { "No game ad available in navigation bridge (process previously killed ?). Navigating back to home screen." }
                navigationBridge.navigateBack()
            }
        }

        navigation(
            route = HomeDestination.InGame.routeName,
            startDestination = InGameDestination.InGameDispatch.routeName
        ) {
            composable(InGameDestination.InGameDispatch.routeName) {
                val viewModel = viewModel<GameViewModel>(factory = getInGameVmFactory())
                DisposableEffect(viewModel) {
                    viewModel.onStart()
                    onDispose { viewModel.onStop() }
                }
                LoadingSpinner()
            }

            composable(InGameDestination.WaitingRoomHost.routeName) {
                WaitingRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(InGameDestination.WaitingRoomGuest.routeName) {
                WaitingRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
            composable(InGameDestination.GameRoomHost.routeName) {
                GameRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(InGameDestination.GameRoomGuest.routeName) {
                GameRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
        }
    }
}
