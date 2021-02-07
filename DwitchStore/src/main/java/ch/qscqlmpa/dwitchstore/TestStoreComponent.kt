package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.store.TestStoreModule
import dagger.Component

@StoreScope
@Component(
    modules = [
        TestStoreModule::class
    ]
)
abstract class TestStoreComponent : StoreComponent {

    internal abstract val db: AppRoomDatabase

    fun clearStore() {
        db.clearAllTables()
    }

    @Component.Factory
    interface Factory {
        fun create(module: TestStoreModule): TestStoreComponent
    }
}
