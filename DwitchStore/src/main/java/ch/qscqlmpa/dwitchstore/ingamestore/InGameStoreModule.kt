package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchstore.db.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.util.SerializerFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

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
        return InGameStoreImpl(gameLocalId, localPlayerLocalId, database, serializerFactory)
    }
}