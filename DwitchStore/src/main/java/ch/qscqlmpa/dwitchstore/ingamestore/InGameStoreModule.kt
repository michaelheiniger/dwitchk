package ch.qscqlmpa.dwitchstore.ingamestore

import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
abstract class InGameStoreModule {
    @InGameStoreScope
    @Binds
    internal abstract fun provideInGameStore(store: InGameStoreImpl): InGameStore

    companion object {
        @InGameStoreScope
        @Provides
        internal fun provideJson(): Json {
            return Json.Default
        }
    }
}
