package ch.qscqlmpa.dwitchstore

import android.content.Context
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreModule
import ch.qscqlmpa.dwitchstore.store.RoomModule
import ch.qscqlmpa.dwitchstore.store.Store
import ch.qscqlmpa.dwitchstore.store.StoreModule
import dagger.BindsInstance
import dagger.Component

@StoreScope
@Component(
    modules = [
        RoomModule::class,
        StoreModule::class
    ]
)
interface StoreComponent {

    val store: Store

    fun addInGameStoreComponent(module: InGameStoreModule): InGameStoreComponent

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): StoreComponent
    }
}
