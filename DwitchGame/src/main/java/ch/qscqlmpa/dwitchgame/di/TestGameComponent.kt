package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameModule
import dagger.Component

@GameScope
@Component(
    modules = [
        DwitchGameModule::class,
        StoreModule::class,
        HomeFacadeModule::class,
        GameAdvertisingFacadeModule::class,
        TestGameDiscoveryModule::class,
        SerializationModule::class,
        SchedulersModule::class,
    ]
)
interface TestGameComponent : GameComponent {

    val networkListener: NetworkAdapter

    fun addTestInGameComponent(module: InGameModule): TestInGameComponent

    @Component.Factory
    interface Factory {
        fun create(dwitchGameModule: DwitchGameModule, storeModule: StoreModule): TestGameComponent
    }
}
