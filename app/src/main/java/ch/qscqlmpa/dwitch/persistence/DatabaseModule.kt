package ch.qscqlmpa.dwitch.persistence

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DatabaseModule {

    @Singleton
    @Binds
    abstract fun provideStore(store: StoreImpl): Store

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideDatabase(context: Context): AppRoomDatabase {
            return Room.databaseBuilder(context, AppRoomDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}