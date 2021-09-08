package ch.qscqlmpa.dwitch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.Dwitch
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import javax.inject.Named

class HomeActivity : AppCompatActivity(), HasAndroidInjector {

    @Named("home")
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var serviceManager: ServiceManager

    @Inject
    lateinit var gameDiscoveryFacade: GameDiscoveryFacade

    @Inject
    lateinit var navigationBridge: NavigationBridge

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContent {
            Dwitch(
                vmFactory = viewModelFactory,
                inGameVmFactory = { (application as App).inGameUiComponent!!.viewModelFactory },
                navigationBridge = navigationBridge
            )
        }
    }
}
