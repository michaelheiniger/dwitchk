package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameModule
import dagger.Component

@GameScope
@Component(
    modules = [
        DwitchGameModule::class,
        StoreModule::class,
        HomeModule::class,
        GameDiscoveryModule::class,
        SerializationModule::class,
        SchedulersModule::class,
    ]
)
interface GameComponent {
    val homeFacade: HomeFacade
    val homeHostFacade: HomeHostFacade
    val homeGuestFacade: HomeGuestFacade

    fun addInGameComponent(module: InGameModule): InGameComponent

    @Component.Factory
    interface Factory {
        fun create(dwitchGameModule: DwitchGameModule, storeModule: StoreModule): GameComponent
    }
}
