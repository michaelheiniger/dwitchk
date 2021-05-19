package ch.qscqlmpa.dwitch.persistence

import dagger.Module

@Suppress("unused")
@Module
abstract class IntTestDatabaseModule {

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
//        fun provideDatabase(): AppRoomDatabase {
//            val context = ApplicationProvider.getApplicationContext<Context>()
//            return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
//                    .fallbackToDestructiveMigration()
//                    .build()
//        }
//    }
}
