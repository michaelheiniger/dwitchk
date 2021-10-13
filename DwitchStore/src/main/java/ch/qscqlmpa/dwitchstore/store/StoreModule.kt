package ch.qscqlmpa.dwitchstore.store

import ch.qscqlmpa.dwitchstore.StoreScope
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class StoreModule {

    @StoreScope
    @Binds
    internal abstract fun provideStore(store: StoreImpl): Store
}
