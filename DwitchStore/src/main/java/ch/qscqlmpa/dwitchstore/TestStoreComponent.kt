package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchstore.store.TestStoreModule
import dagger.Component

@StoreScope
@Component(modules = [
    TestStoreModule::class
])
interface TestStoreComponent : StoreComponent{

    @Component.Factory
    interface Factory {
        fun create(module: TestStoreModule): TestStoreComponent
    }
}