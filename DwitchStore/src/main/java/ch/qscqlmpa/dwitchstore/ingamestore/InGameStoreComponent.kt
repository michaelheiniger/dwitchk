package ch.qscqlmpa.dwitchstore.ingamestore

import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@InGameStoreScope
@Subcomponent(
    modules = [
        InGameStoreModule::class
    ]
)
interface InGameStoreComponent {
    val inGameStore: InGameStore

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance @Named("gameLocalId") gameLocalId: Long,
            @BindsInstance @Named("localPlayerLocalId") localPlayerLocalId: Long
        ): InGameStoreComponent
    }
}
