package ch.qscqlmpa.dwitch.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameScreen
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameScreen
import ch.qscqlmpa.dwitch.ui.home.main.HomeScreen
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory

enum class DwitchScreenEnum() {
    Home,
    HostNewGame,
    JoinNewGame,
//    WaitingRoomHost,
//    WaitingRoomGuest,
//    GameRoomHost,
//    GameRoomGuest
}

@Composable
fun DwitchApp(vmFactory: ViewModelFactory) {
    DwitchTheme {
        val navController = rememberNavController()
//        val backstackEntry = navController.currentBackStackEntryAsState()
        Scaffold { innerPadding ->
            DwitchNavHost(
                vmFactory = vmFactory,
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun DwitchNavHost(
    vmFactory: ViewModelFactory,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DwitchScreenEnum.Home.name,
        modifier = modifier
    ) {
        composable(DwitchScreenEnum.Home.name) {
            HomeScreen(
                vmFactory = vmFactory,
                onCreateNewGameClick = { navController.navigate(DwitchScreenEnum.HostNewGame.name) },
                onJoinNewGameEvent = { game ->
                    navController.navigate("${DwitchScreenEnum.JoinNewGame.name}/${game.gameIpAddress}")
                }
            )
        }
        composable(DwitchScreenEnum.HostNewGame.name) {
            HostNewGameScreen(
                vmFactory = vmFactory,
                onHostGameClick = { /* TODO: Nav to WaitingRoomScreen */ },
                onBackClick = { navController.navigate(DwitchScreenEnum.Home.name) }
            )
        }
        composable(
            route = "${DwitchScreenEnum.JoinNewGame.name}/{gameIpAddress}",
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
                onJoinGameClick = { /* TODO: nav to WaitingRoomScreen */ },
                onBackClick = { navController.navigate(DwitchScreenEnum.Home.name) }
            )
        }
//        composable(DwitchScreenEnum.WaitingRoomHost.name) {
//            WaitingRoomHostScreen(
//
//            )
//        }
//        composable(DwitchScreenEnum.WaitingRoomGuest.name) {
//            WaitingRoomGuestScreen(
//
//            )
//        }
//        composable(DwitchScreenEnum.GameRoomHost.name) {
//            GameRoomHostScreen(
//
//            )
//        }
//        composable(DwitchScreenEnum.GameRoomGuest.name) {
//            GameRoomGuestScreen(
//
//            )
//        }
    }
}