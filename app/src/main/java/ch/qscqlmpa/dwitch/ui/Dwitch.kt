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
import ch.qscqlmpa.dwitch.ui.navigation.GameScreens
import ch.qscqlmpa.dwitch.ui.navigation.HandleNavigation
import ch.qscqlmpa.dwitch.ui.navigation.HomeScreens
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
        startDestination = HomeScreens.Home.routeName,
        modifier = modifier
    ) {
        composable(HomeScreens.Home.routeName) {
            HomeScreen(vmFactory = vmFactory)
        }
        composable(HomeScreens.HostNewGame.routeName) {
            HostNewGameScreen(vmFactory = vmFactory)
        }
        composable(route = HomeScreens.JoinNewGame.routeName) { entry ->
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
            route = HomeScreens.InGame.routeName,
            startDestination = GameScreens.GameDispatch.routeName
        ) {
            composable(GameScreens.GameDispatch.routeName) {
                val viewModel = viewModel<GameViewModel>(factory = getInGameVmFactory())
                DisposableEffect(viewModel) {
                    viewModel.onStart()
                    onDispose { viewModel.onStop() }
                }
                LoadingSpinner()
            }

            composable(GameScreens.WaitingRoomHost.routeName) {
                WaitingRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(GameScreens.WaitingRoomGuest.routeName) {
                WaitingRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
            composable(GameScreens.GameRoomHost.routeName) {
                GameRoomHostScreen(vmFactory = getInGameVmFactory())
            }
            composable(GameScreens.GameRoomGuest.routeName) {
                GameRoomGuestScreen(vmFactory = getInGameVmFactory())
            }
        }
    }
}
