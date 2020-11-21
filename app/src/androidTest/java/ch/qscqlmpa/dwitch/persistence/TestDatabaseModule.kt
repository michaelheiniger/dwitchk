package ch.qscqlmpa.dwitch.persistence

import dagger.Module

@Module
abstract class TestDatabaseModule {

//    @Binds
//    @Singleton
//    abstract fun provideStore(store: StoreImpl): Store
//
//    @Module
//    companion object {
//
//        @JvmStatic
//        @Provides
//        @Singleton
//        fun provideDatabase(context: Context): AppRoomDatabase {
//            return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
//                    .fallbackToDestructiveMigration()
//                    .build()
//        }
//    }
}