package ch.qscqlmpa.dwitch.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
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
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestBody
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest.WaitingRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostBody
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host.WaitingRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameGuestDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameHostDestination
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.tinylog.Logger

@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Dwitch(
    createMainActivityComponent: () -> MainActivityComponent,
    createInGameHostUiComponent: (MainActivityComponent) -> InGameHostUiComponent,
    createInGameGuestUiComponent: (MainActivityComponent) -> InGameGuestUiComponent
) {
    val navController = rememberAnimatedNavController()

    // Tied to activity's lifecycle. Defines @ActivityScope Dagger scope
    val mainActivityComponent = daggerUiScopedComponent(componentFactory = { createMainActivityComponent() })

    // Lifecycle of NavController is lifecycle of Dwitch Composable (a new one is recreated after a config. change)
    // which is shorter than the lifecycle of the MainActivityComponent (Activity's lifecycle, surviving config. changes)
    mainActivityComponent.screenNavigator.setNavHostController(navController)

    val darkThemeEnabled = remember { mutableStateOf(false) }

    DwitchTheme(darkTheme = darkThemeEnabled.value) {
        SetupSystemBars()

        DwitchNavHost(
            mainActivityComponent = mainActivityComponent,
            createInGameHostUiComponent = createInGameHostUiComponent,
            createInGameGuestUiComponent = createInGameGuestUiComponent,
            navHostController = navController,
            toggleDarkTheme = { darkThemeEnabled.value = !darkThemeEnabled.value }
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DwitchNavHost(
    mainActivityComponent: MainActivityComponent,
    createInGameHostUiComponent: (MainActivityComponent) -> InGameHostUiComponent,
    createInGameGuestUiComponent: (MainActivityComponent) -> InGameGuestUiComponent,
    navHostController: NavHostController,
    toggleDarkTheme: () -> Unit
) {
    val mainVmFactory = mainActivityComponent.mainViewModelFactory
    val screenNavigator = mainActivityComponent.screenNavigator
    AnimatedNavHost(
        navController = navHostController,
        startDestination = HomeDestination.Home.routeName,
    ) {
        // Main graph destinations
        composable(
            route = HomeDestination.Home.routeName,
            enterTransition = { _, _ -> enterTransition() },
            exitTransition = { _, _ -> exitTransition() }
        ) {
            val homeViewModel = viewModel<HomeViewModel>(factory = mainVmFactory)
            HomeScreen(
                homeViewModel = homeViewModel,
                toggleDarkTheme = toggleDarkTheme
            )
        }
        composable(
            route = HomeDestination.HostNewGame.routeName,
            enterTransition = { _, _ -> enterTransition() },
            exitTransition = { _, _ -> exitTransition() }
        ) {
            val hostNewGameViewModel = viewModel<HostNewGameViewModel>(factory = mainVmFactory)
            HostNewGameScreen(hostNewGameViewModel)
        }
        composable(
            route = HomeDestination.JoinNewGame.routeName,
            enterTransition = { _, _ -> enterTransition() },
            exitTransition = { _, _ -> exitTransition() }
        ) {
            val joinNewGameViewModel = viewModel<JoinNewGameViewModel>(factory = mainVmFactory)
            val gameAd = screenNavigator.getData(HomeDestination.JoinNewGame.gameParamName) as GameAdvertisingInfo?
            if (gameAd != null) {
                JoinNewGameScreen(
                    joinNewGameViewModel = joinNewGameViewModel,
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
            composable(
                route = InGameGuestDestination.Home.routeName,
                enterTransition = { _, _ -> enterTransition() },
                exitTransition = { _, _ -> exitTransition() }
            ) {
                LoadingSpinner()
            }
            composable(
                route = InGameGuestDestination.WaitingRoom.routeName,
                enterTransition = { _, _ -> enterTransition() },
                exitTransition = { _, _ -> exitTransition() }
            ) { entry ->
                val vmFactory =
                    getInGameGuestUiVmFactory(entry, navHostController, mainActivityComponent, createInGameGuestUiComponent)
                val waitingRoomViewModel = viewModel<WaitingRoomViewModel>(factory = vmFactory)
                val guestViewModel = viewModel<WaitingRoomGuestViewModel>(factory = vmFactory)
                val connectionViewModel = viewModel<ConnectionGuestViewModel>(factory = vmFactory)
                WaitingRoomGuestBody(
                    waitingRoomViewModel,
                    guestViewModel,
                    connectionViewModel
                )
            }
            composable(
                route = InGameGuestDestination.GameRoom.routeName,
                enterTransition = { _, _ -> enterTransition() },
                exitTransition = { _, _ -> exitTransition() }
            ) { entry ->
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
            composable(
                route = InGameHostDestination.Home.routeName,
                enterTransition = { _, _ -> enterTransition() },
                exitTransition = { _, _ -> exitTransition() }
            ) {
                LoadingSpinner()
            }
            composable(
                route = InGameHostDestination.WaitingRoom.routeName,
                enterTransition = { _, _ -> enterTransition() },
                exitTransition = { _, _ -> exitTransition() }
            ) { entry ->
                val vmFactory =
                    getInGameHostUiVmFactory(entry, navHostController, mainActivityComponent, createInGameHostUiComponent)
                val waitingRoomViewModel = viewModel<WaitingRoomViewModel>(factory = vmFactory)
                val hostViewModel = viewModel<WaitingRoomHostViewModel>(factory = vmFactory)
                val connectionViewModel = viewModel<ConnectionHostViewModel>(factory = vmFactory)
                WaitingRoomHostBody(
                    waitingRoomViewModel,
                    hostViewModel,
                    connectionViewModel
                )
            }
            composable(
                route = InGameHostDestination.GameRoom.routeName,
                enterTransition = { _, _ -> enterTransition() },
                exitTransition = { _, _ -> exitTransition() }
            ) { entry ->
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

@Composable
private fun SetupSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val sytemBarsColor = MaterialTheme.colors.primarySurface

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = sytemBarsColor,
            darkIcons = useDarkIcons
        )
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

//    val animationSpec = spring<IntOffset>(dampingRatio = Spring.DampingRatioMediumBouncy)
val animationSpec = tween<IntOffset>(durationMillis = 1000, easing = CubicBezierEasing(0.08f, 0.93f, 0.68f, 1.27f))

@OptIn(ExperimentalAnimationApi::class)
private fun enterTransition() = slideInHorizontally(
    initialOffsetX = { width -> width },
    animationSpec = animationSpec
)

@OptIn(ExperimentalAnimationApi::class)
private fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { width -> width },
    animationSpec = animationSpec
)
