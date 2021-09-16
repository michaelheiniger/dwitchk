package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameHostComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameGuestModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameHostModule
import dagger.Component

@GameScope
@Component(
    modules = [
        DwitchGameModule::class,
        StoreModule::class,
        GameFacadeModule::class,
        GameLifecycleModule::class,
        GameAdvertisingModule::class,
        TestGameDiscoveryModule::class,
        SerializationModule::class,
        SchedulersModule::class,
    ]
)
interface TestGameComponent : GameComponent {

    val networkListener: NetworkAdapter

    fun addTestInGameHostComponent(module: InGameHostModule): TestInGameHostComponent
    fun addTestInGameGuestComponent(module: InGameGuestModule): TestInGameGuestComponent

    @Component.Factory
    interface Factory {
        fun create(dwitchGameModule: DwitchGameModule, storeModule: StoreModule): TestGameComponent
    }
}
