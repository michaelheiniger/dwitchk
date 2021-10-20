package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.InGameHostComponent
import ch.qscqlmpa.dwitchstore.StoreComponent
import dagger.BindsInstance
import dagger.Component

@GameScope
@Component(
    dependencies = [
        CommunicationComponent::class,
        StoreComponent::class
    ],
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

    fun getInGameHostComponentFactory(): InGameHostComponent.Factory
    fun getInGameGuestComponentFactory(): InGameGuestComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance idlingResource: DwitchIdlingResource,
            communicationComponent: CommunicationComponent,
            storeComponent: StoreComponent
        ): GameComponent
    }
}
