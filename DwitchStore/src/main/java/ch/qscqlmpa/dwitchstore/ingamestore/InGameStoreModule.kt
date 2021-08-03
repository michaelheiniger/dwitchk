package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.util.SerializerFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
class InGameStoreModule(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long
) {

    @InGameStoreScope
    @Provides
    internal fun provideJson(): Json {
        return Json.Default
    }

    @InGameStoreScope
    @Provides
    internal fun provideInGameStore(database: AppRoomDatabase, serializerFactory: SerializerFactory): InGameStore {
        return InGameStoreImpl(database, gameLocalId, localPlayerLocalId, serializerFactory)
    }
}
