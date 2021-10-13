package ch.qscqlmpa.dwitchstore.store

import android.content.Context
import androidx.room.Room
import ch.qscqlmpa.dwitchstore.StoreScope
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal object RoomModule {

    @StoreScope
    @Provides
    internal fun provideDatabase(context: Context): AppRoomDatabase {
        return Room.databaseBuilder(context, AppRoomDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }
}