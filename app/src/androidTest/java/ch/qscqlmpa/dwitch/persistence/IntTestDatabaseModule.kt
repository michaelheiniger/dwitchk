package ch.qscqlmpa.dwitch.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ch.qscqlmpa.dwitch.persistence.AppRoomDatabase
import ch.qscqlmpa.dwitch.persistence.Store
import ch.qscqlmpa.dwitch.persistence.StoreImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class IntTestDatabaseModule {

    @Binds
    @Singleton
    abstract fun provideStore(store: StoreImpl): Store

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideDatabase(): AppRoomDatabase {
            val context = ApplicationProvider.getApplicationContext<Context>()
            return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}