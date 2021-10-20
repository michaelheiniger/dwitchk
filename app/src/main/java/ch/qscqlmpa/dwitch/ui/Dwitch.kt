package ch.qscqlmpa.dwitch.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import ch.qscqlmpa.dwitch.MainActivityComponent
import ch.qscqlmpa.dwitch.daggerUiScopedComponent
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ui.home.home.HomeScreen
import ch.qscqlmpa.dwitch.ui.home.home.HomeViewModel
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameScreen
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameViewModel
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameScreen
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameViewModel
import ch.qscqlmpa.dwitch.ui.ingame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ingame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.host.GameRoomHostScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.host.GameRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameGuestDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameHostDestination
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import org.tinylog.Logger

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Dwitch(
    createMainActivityComponent: (NavHostController) -> MainActivityComponent,
    createInGameHostUiComponent: (MainActivityComponent) -> InGameHostUiComponent,
    createInGameGuestUiComponent: (MainActivityComponent) -> InGameGuestUiComponent,
    finish: () -> Unit
) {
    DwitchTheme {
        val navController = rememberNavController()

        // Tied to activity's lifecycle. Defines @ActivityScope Dagger scope
        val mainActivityComponent = daggerUiScopedComponent(
            componentFactory = { createMainActivityComponent(navController) }
        )

        Scaffold { innerPadding ->
            DwitchNavHost(
                mainActivityComponent = mainActivityComponent,
                createInGameHostUiComponent = createInGameHostUiComponent,
                createInGameGuestUiComponent = createInGameGuestUiComponent,
                navHostController = navController,
                modifier = Modifier.padding(innerPadding),
                finish = finish
            )
        }
    }
}

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DwitchNavHost(
    mainActivityComponent: MainActivityComponent,
    createInGameHostUiComponent: (MainActivityComponent) -> InGameHostUiComponent,
    createInGameGuestUiComponent: (MainActivityComponent) -> InGameGuestUiComponent,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    finish: () -> Unit
) {
    val mainVmFactory = mainActivityComponent.mainViewModelFactory
    val screenNavigator = mainActivityComponent.screenNavigator
    BackHandler { finish() }
    NavHost(
        navController = navHostController,
        startDestination = HomeDestination.Home.routeName,
        modifier = modifier
    ) {
        // Main graph destinations
        composable(HomeDestination.Home.routeName) {
            val homeViewModel = viewModel<HomeViewModel>(factory = mainVmFactory)
            HomeScreen(homeViewModel)
        }
        composable(HomeDestination.HostNewGame.routeName) {
            val hostNewGameViewModel = viewModel<HostNewGameViewModel>(factory = mainVmFactory)
            HostNewGameScreen(hostNewGameViewModel)
        }
        composable(route = HomeDestination.JoinNewGame.routeName) {
            val joinNewGameViewModel = viewModel<JoinNewGameViewModel>(factory = mainVmFactory)
            val gameAd = screenNavigator.getData(HomeDestination.JoinNewGame.gameParamName) as GameAdvertisingInfo?
            if (gameAd != null) {
                JoinNewGameScreen(
                    joinNewGameViewModel,
                    gameAd = gameAd
                )
            } else {
                Logger.warn { "No game ad available in navigation bridge (process previously killed ?). Navigating back to home screen." }
                screenNavigator.navigateBack()
            }
        }

        // In-game guest graph destinations
        navigation(
            route = HomeDestination.InGameGuest.routeName,
            startDestination = InGameGuestDestination.Home.routeName
        ) {
            composable(InGameGuestDestination.Home.routeName) {
                LoadingSpinner()
            }
            composable(InGameGuestDestination.WaitingRoom.routeName) { entry ->
                val vmFactory =
                    getInGameGuestUiVmFactory(entry, navHostController, mainActivityComponent, createInGameGuestUiComponent)
                val waitingRoomViewModel = viewModel<WaitingRoomViewModel>(factory = vmFactory)
                val guestViewModel = viewModel<WaitingRoomGuestViewModel>(factory = vmFactory)
                val connectionViewModel = viewModel<ConnectionGuestViewModel>(factory = vmFactory)
                WaitingRoomGuestScreen(
                    waitingRoomViewModel,
                    guestViewModel,
                    connectionViewModel
                )
            }
            composable(InGameGuestDestination.GameRoom.routeName) { entry ->
                val vmFactory =
                    getInGameGuestUiVmFactory(entry, navHostController, mainActivityComponent, createInGameGuestUiComponent)
                val playerViewModel = viewModel<GameRoomViewModel>(factory = vmFactory)
                val guestViewModel = viewModel<GameRoomGuestViewModel>(factory = vmFactory)
                val connectionViewModel = viewModel<ConnectionGuestViewModel>(factory = vmFactory)
                GameRoomGuestScreen(
                    playerViewModel,
                    guestViewModel,
                    connectionViewModel
                )
            }
        }
        // In-game host graph destinations
        navigation(
            route = HomeDestination.InGameHost.routeName,
            startDestination = InGameHostDestination.Home.routeName
        ) {
            composable(InGameHostDestination.Home.routeName) {
                LoadingSpinner()
            }
            composable(InGameHostDestination.WaitingRoom.routeName) { entry ->
                val vmFactory =
                    getInGameHostUiVmFactory(entry, navHostController, mainActivityComponent, createInGameHostUiComponent)
                val waitingRoomViewModel = viewModel<WaitingRoomViewModel>(factory = vmFactory)
                val hostViewModel = viewModel<WaitingRoomHostViewModel>(factory = vmFactory)
                val connectionViewModel = viewModel<ConnectionHostViewModel>(factory = vmFactory)
                WaitingRoomHostScreen(
                    waitingRoomViewModel,
                    hostViewModel,
                    connectionViewModel
                )
            }
            composable(InGameHostDestination.GameRoom.routeName) { entry ->
                val vmFactory =
                    getInGameHostUiVmFactory(entry, navHostController, mainActivityComponent, createInGameHostUiComponent)
                val playerViewModel = viewModel<GameRoomViewModel>(factory = vmFactory)
                val hostViewModel = viewModel<GameRoomHostViewModel>(factory = vmFactory)
                val connectionViewModel = viewModel<ConnectionHostViewModel>(factory = vmFactory)
                GameRoomHostScreen(
                    playerViewModel,
                    hostViewModel,
                    connectionViewModel
                )
            }
        }
    }
}

/**
 * To be used in destinations of In-Game sub navigation graph.
 */
@Composable
private fun getInGameHostUiVmFactory(
    currentNavBackStackEntry: NavBackStackEntry,
    navHostController: NavHostController,
    mainActivityComponent: MainActivityComponent,
    createInGameHostUiComponent: (MainActivityComponent) -> InGameHostUiComponent
): ViewModelFactory {
    val parentId = currentNavBackStackEntry.destination.parent!!.id
    val inGameNavGraphBackStackEntry = remember { navHostController.getBackStackEntry(parentId) }

    // Dagger component scoped to in-game navigation sub-graph's lifecycle. Defines @InGameUIScope Dagger scope.
    val inGameUiComponent = daggerUiScopedComponent(
        viewModelStoreOwner = inGameNavGraphBackStackEntry,
        componentFactory = { createInGameHostUiComponent(mainActivityComponent) }
    )

    // Factory of actual UI ViewModels
    return inGameUiComponent.viewModelFactory
}

/**
 * To be used in destinations of In-Game sub navigation graph.
 */
@Composable
private fun getInGameGuestUiVmFactory(
    currentNavBackStackEntry: NavBackStackEntry,
    navHostController: NavHostController,
    mainActivityComponent: MainActivityComponent,
    createInGameGuestUiComponent: (MainActivityComponent) -> InGameGuestUiComponent
): ViewModelFactory {
    val parentId = currentNavBackStackEntry.destination.parent!!.id
    val inGameNavGraphBackStackEntry = remember { navHostController.getBackStackEntry(parentId) }

    // Dagger component scoped to in-game navigation sub-graph's lifecycle. Defines @InGameUIScope Dagger scope.
    val inGameUiComponent = daggerUiScopedComponent(
        viewModelStoreOwner = inGameNavGraphBackStackEntry,
        componentFactory = { createInGameGuestUiComponent(mainActivityComponent) }
    )

    // Factory of actual UI ViewModels
    return inGameUiComponent.viewModelFactory
}
