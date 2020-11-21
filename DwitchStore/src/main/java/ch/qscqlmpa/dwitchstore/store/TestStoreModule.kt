package ch.qscqlmpa.dwitchstore.store

import android.content.Context
import androidx.room.Room
import ch.qscqlmpa.dwitchstore.StoreScope
import ch.qscqlmpa.dwitchstore.db.AppRoomDatabase
import dagger.Module
import dagger.Provides

@Module
class TestStoreModule(private val context: Context) {

    @StoreScope
    @Provides
    internal fun provideDatabase(): AppRoomDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @StoreScope
    @Provides
    internal fun provideStore(appRoomDatabase: AppRoomDatabase): Store {
        return StoreImpl(appRoomDatabase)
    }
}