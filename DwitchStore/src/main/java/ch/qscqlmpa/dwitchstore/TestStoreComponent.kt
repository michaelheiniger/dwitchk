package ch.qscqlmpa.dwitchstore

import android.content.Context
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.store.StoreModule
import ch.qscqlmpa.dwitchstore.store.TestRoomModule
import dagger.BindsInstance
import dagger.Component

@StoreScope
@Component(
    modules = [
        TestRoomModule::class,
        StoreModule::class
    ]
)
abstract class TestStoreComponent : StoreComponent {

    internal abstract val db: AppRoomDatabase

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TestStoreComponent
    }
}
