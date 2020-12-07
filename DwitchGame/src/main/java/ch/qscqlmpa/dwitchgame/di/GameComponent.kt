package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameComponent
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.OngoingGameModule
import dagger.Component

@GameScope
@Component(
    modules = [
        GameModule::class,
        HomeModule::class,
        GameDiscoveryModule::class,
        SerializationModule::class,
        SchedulersModule::class,
    ]
)
interface GameComponent {
    val appEventRepository: AppEventRepository
    val advertisedGameRepository: AdvertisedGameRepository
    val homeHostFacade: HomeHostFacade
    val homeGuestFacade: HomeGuestFacade

    fun addOngoingGameComponent(module: OngoingGameModule): OngoingGameComponent

    @Component.Factory
    interface Factory {
        fun create(module: GameModule): GameComponent
    }
}