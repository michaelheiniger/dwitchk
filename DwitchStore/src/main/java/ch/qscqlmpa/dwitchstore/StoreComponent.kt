package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreModule
import ch.qscqlmpa.dwitchstore.store.Store
import ch.qscqlmpa.dwitchstore.store.StoreModule
import dagger.Component

@StoreScope
@Component(modules = [
    StoreModule::class,
])
interface StoreComponent {

    val store: Store

    fun addInGameStoreComponent(module: InGameStoreModule): InGameStoreComponent

    @Component.Factory
    interface Factory {
        fun create(module: StoreModule): StoreComponent
    }
}