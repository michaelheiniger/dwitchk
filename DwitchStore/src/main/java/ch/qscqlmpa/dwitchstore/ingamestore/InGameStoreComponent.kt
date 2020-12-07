package ch.qscqlmpa.dwitchstore.ingamestore

import dagger.Subcomponent

@InGameStoreScope
@Subcomponent(modules = [
    InGameStoreModule::class
])
interface InGameStoreComponent {
    val inGameStore: InGameStore
}