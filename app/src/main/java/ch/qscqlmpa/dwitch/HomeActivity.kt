package ch.qscqlmpa.dwitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.Dwitch
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import javax.inject.Inject
import javax.inject.Named

class HomeActivity : ComponentActivity() {

    @Named("home")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var serviceManager: ServiceManager

    @Inject
    lateinit var gameDiscoveryFacade: GameDiscoveryFacade

    @Inject
    lateinit var navigationBridge: NavigationBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).inject(this)
        super.onCreate(savedInstanceState)
        setContent {
            Dwitch(
                vmFactory = viewModelFactory,
                inGameVmFactory = { (application as App).inGameViewModelFactory!! },
                navigationBridge = navigationBridge
            )
        }
    }
}
