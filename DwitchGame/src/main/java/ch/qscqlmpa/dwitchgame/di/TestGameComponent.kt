package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchgame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameHostComponent
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
interface TestGameComponent : GameComponent {

    fun getTestInGameHostComponentFactory(): TestInGameHostComponent.Factory
    fun getTestInGameGuestComponentFactory(): TestInGameGuestComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance idlingResource: DwitchIdlingResource,
            communicationComponent: CommunicationComponent,
            storeComponent: StoreComponent
        ): TestGameComponent
    }
}
