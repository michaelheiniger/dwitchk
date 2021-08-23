package ch.qscqlmpa.dwitch.ui.ingame

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
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.GameViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomGuestDestination
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.host.GameRoomHostDestination
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.host.GameRoomHostScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestDestination
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostDestination
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostScreen
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory

sealed class GameScreens(val name: String) {
    object Loading : GameScreens("loading")
    object WaitingRoomHost : GameScreens("waitingRoomHost")
    object WaitingRoomGuest : GameScreens("waitingRoomGuest")
    object GameRoomHost : GameScreens("gameRoomHost")
    object GameRoomGuest : GameScreens("gameRoomGuest")
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun DwitchGame(
    vmFactory: ViewModelFactory,
    navigateToHomeFragment: () -> Unit
) {
    DwitchTheme {
        val navController = rememberNavController()
        val viewModel = viewModel<GameViewModel>(factory = vmFactory)
        DisposableEffect(key1 = viewModel) {
            viewModel.onStart()
            onDispose { viewModel.onStop() }
        }

        Scaffold { innerPadding ->
            when (val startScreen = viewModel.startScreen.value) {
                GameScreens.Loading -> LoadingSpinner()
                else -> GameNavHost(
                    vmFactory = vmFactory,
                    navController = navController,
                    startScreen = startScreen,
                    modifier = Modifier.padding(innerPadding),
                    navigateToHomeFragment = navigateToHomeFragment
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun GameNavHost(
    vmFactory: ViewModelFactory,
    navController: NavHostController,
    startScreen: GameScreens,
    modifier: Modifier = Modifier,
    navigateToHomeFragment: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startScreen.name,
        modifier = modifier
    ) {
        composable(GameScreens.WaitingRoomHost.name) {
            WaitingRoomHostScreen(
                vmFactory = vmFactory,
                onNavigationEvent = { event ->
                    when (event) {
                        WaitingRoomHostDestination.NavigateToGameRoomScreen ->
                            navController.navigate(GameScreens.GameRoomHost.name)
                        WaitingRoomHostDestination.NavigateToHomeScreen -> navigateToHomeFragment()
                        WaitingRoomHostDestination.CurrentScreen -> {
                            // Nothing to do
                        }
                    }
                }
            )
        }
        composable(GameScreens.WaitingRoomGuest.name) {
            WaitingRoomGuestScreen(
                vmFactory = vmFactory,
                onNavigationEvent = { event ->
                    when (event) {
                        WaitingRoomGuestDestination.NavigateToGameRoomScreen ->
                            navController.navigate(GameScreens.GameRoomGuest.name)
                        WaitingRoomGuestDestination.NavigateToHomeScreen -> navigateToHomeFragment()
                        WaitingRoomGuestDestination.CurrentScreen -> {
                            // Nothing to do
                        }
                    }
                }
            )
        }
        composable(GameScreens.GameRoomHost.name) {
            GameRoomHostScreen(
                vmFactory = vmFactory,
                onNavigationEvent = { event ->
                    when (event) {
                        GameRoomHostDestination.NavigateToHomeScreen -> navigateToHomeFragment()
                        GameRoomHostDestination.CurrentScreen -> {
                            // Nothing to do
                        }
                    }
                }
            )
        }
        composable(GameScreens.GameRoomGuest.name) {
            GameRoomGuestScreen(
                vmFactory = vmFactory,
                onNavigationEvent = { event ->
                    when (event) {
                        GameRoomGuestDestination.NavigateToHomeScreen -> navigateToHomeFragment()
                        GameRoomGuestDestination.CurrentScreen -> {
                            // Nothing to do
                        }
                    }
                }
            )
        }
    }
}
