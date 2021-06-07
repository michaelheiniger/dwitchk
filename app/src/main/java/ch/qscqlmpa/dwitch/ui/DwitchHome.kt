package ch.qscqlmpa.dwitch.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ch.qscqlmpa.dwitch.ui.home.home.HomeScreen
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameScreen
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameScreen
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory

sealed class HomeScreens(val name: String) {
    object Home : HomeScreens("home")
    object HostNewGame : HomeScreens("hostNewGame")
    object JoinNewGame : HomeScreens("joinNewGame")
}

@Composable
fun DwitchHome(
    vmFactory: ViewModelFactory,
    navigateToGameFragment: () -> Unit
) {
    DwitchTheme {
        val navController = rememberNavController()
        Scaffold { innerPadding ->
            HomeNavHost(
                vmFactory = vmFactory,
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                navigateToGameFragment = navigateToGameFragment
            )
        }
    }
}

@Composable
private fun HomeNavHost(
    vmFactory: ViewModelFactory,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateToGameFragment: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreens.Home.name,
        modifier = modifier
    ) {
        composable(HomeScreens.Home.name) {
            HomeScreen(
                vmFactory = vmFactory,
                onCreateNewGameClick = { navController.navigate(HomeScreens.HostNewGame.name) },
                onJoinNewGameEvent = { game ->
                    navController.popUpToInclusiveAndNavigate(
                        route = "${HomeScreens.JoinNewGame.name}/${game.gameIpAddress}"
                    )
                },
                onNavigateToGame = navigateToGameFragment
            )
        }
        composable(HomeScreens.HostNewGame.name) {
            HostNewGameScreen(
                vmFactory = vmFactory,
                onHostGameClick = navigateToGameFragment,
                onBackClick = { navController.popUpToInclusiveAndNavigate(HomeScreens.Home.name) }
            )
        }
        composable(
            route = "${HomeScreens.JoinNewGame.name}/{gameIpAddress}",
//            arguments = listOf(
//                navArgument("game") {
//                    type = NavType.ParcelableType(AdvertisedGame::class.java)
//                }
//            )
        ) { entry ->
            val gameIpAddress = entry.arguments?.getString("gameIpAddress")!!
            JoinNewGameScreen(
                vmFactory = vmFactory,
                gameIpAddress = gameIpAddress,
                onJoinGameClick = navigateToGameFragment,
                onBackClick = { navController.popUpToInclusiveAndNavigate(HomeScreens.Home.name) }
            )
        }
    }
}

private fun NavController.popUpToInclusiveAndNavigate(route: String) {
    navigate(
        route = route,
        navOptions = navOptions { popUpTo(HomeScreens.Home.name) { inclusive = true } }
    )
}