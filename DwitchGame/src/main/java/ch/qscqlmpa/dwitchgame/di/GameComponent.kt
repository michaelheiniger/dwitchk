package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.InGameHostComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameGuestModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameHostModule
import ch.qscqlmpa.dwitchstore.store.Store
import dagger.BindsInstance
import dagger.Component

@GameScope
@Component(
    modules = [
        UtilsModule::class,
        GameFacadeModule::class,
        GameLifecycleModule::class,
        GameDiscoveryModule::class,
        SchedulersModule::class,
    ]
)
interface GameComponent {
    val gameLifecycleFacade: GameLifecycleFacade
    val gameFacade: GameFacade
    val gameDiscoveryFacade: GameDiscoveryFacade

    fun addInGameHostComponent(module: InGameHostModule): InGameHostComponent
    fun addInGameGuestComponent(module: InGameGuestModule): InGameGuestComponent

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance idlingResource: DwitchIdlingResource,
            @BindsInstance store: Store,
            @BindsInstance gameDiscovery: GameDiscovery,
            @BindsInstance deviceConnectivityRepository: DeviceConnectivityRepository
        ): GameComponent
    }
}
