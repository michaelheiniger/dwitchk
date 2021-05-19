package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchstore.store.Store
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class StoreModule(
    private val store: Store
) {

    @GameScope
    @Provides
    fun provideStore(): Store {
        return store
    }
}
