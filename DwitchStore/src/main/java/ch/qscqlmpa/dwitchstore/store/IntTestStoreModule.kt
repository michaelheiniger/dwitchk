package ch.qscqlmpa.dwitchstore.store

import dagger.Module

@Module
abstract class IntTestStoreModule {

//    @Binds
//    @StoreScope
//    internal abstract fun provideStore(store: StoreImpl): Store
//
//    @Module
//    companion object {
//
//        @JvmStatic
//        @Provides
//        @StoreScope
//        fun provideDatabase(): AppRoomDatabase {
//            val context = ApplicationProvider.getApplicationContext<Context>()
//            return Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java)
//                    .fallbackToDestructiveMigration()
//                    .build()
//        }
//    }
}