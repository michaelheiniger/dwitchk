package ch.qscqlmpa.dwitchstore.store

import android.content.Context
import androidx.room.Room
import ch.qscqlmpa.dwitchstore.StoreScope
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal object TestRoomModule {

    @StoreScope
    @Provides
    internal fun provideDatabase(context: Context): AppRoomDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }
}
