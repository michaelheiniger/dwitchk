package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameHostComponent
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
interface TestGameComponent : GameComponent {

    fun addTestInGameHostComponent(module: InGameHostModule): TestInGameHostComponent
    fun addTestInGameGuestComponent(module: InGameGuestModule): TestInGameGuestComponent

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance idlingResource: DwitchIdlingResource,
            @BindsInstance store: Store,
            @BindsInstance gameDiscovery: GameDiscovery,
            @BindsInstance deviceConnectivityRepository: DeviceConnectivityRepository
        ): TestGameComponent
    }
}
