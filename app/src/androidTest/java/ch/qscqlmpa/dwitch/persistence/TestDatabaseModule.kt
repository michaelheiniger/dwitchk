package ch.qscqlmpa.dwitch.persistence

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class TestDatabaseModule {

    @Binds
    @Singleton
    abstract fun provideStore(store: StoreImpl): Store

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideDatabase(context: Context): AppRoomDatabase {
            return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}