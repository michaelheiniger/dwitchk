package ch.qscqlmpa.dwitchstore.store

import android.content.Context
import androidx.room.Room
import ch.qscqlmpa.dwitchstore.StoreScope
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import dagger.Module
import dagger.Provides

@Module
class StoreModule(private val context: Context) {

    @StoreScope
    @Provides
    internal fun provideDatabase(): AppRoomDatabase {
        return Room.databaseBuilder(context, AppRoomDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @StoreScope
    @Provides
    internal fun provideStore(appRoomDatabase: AppRoomDatabase): Store {
        return StoreImpl(appRoomDatabase)
    }
}
