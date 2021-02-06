package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.di.modules.HomeModule
import ch.qscqlmpa.dwitchgame.di.modules.SchedulersModule
import ch.qscqlmpa.dwitchgame.di.modules.SerializationModule
import ch.qscqlmpa.dwitchgame.di.modules.StoreModule
import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.ongoinggame.di.TestOngoingGameComponent
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.OngoingGameModule
import dagger.Component

@GameScope
@Component(
    modules = [
        StoreModule::class,
        HomeModule::class,
        TestGameDiscoveryModule::class,
        SerializationModule::class,
        SchedulersModule::class,
    ]
)
interface TestGameComponent : GameComponent {

    val networkListener: NetworkAdapter

    fun addTestOngoingGameComponent(module: OngoingGameModule): TestOngoingGameComponent

    @Component.Factory
    interface Factory {
        fun create(module: StoreModule): TestGameComponent
    }
}
