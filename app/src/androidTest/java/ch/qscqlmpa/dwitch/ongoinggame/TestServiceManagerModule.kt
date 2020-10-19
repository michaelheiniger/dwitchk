package ch.qscqlmpa.dwitch.ongoinggame

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class TestServiceManagerModule {

    @Singleton
    @Binds
    abstract fun bindServiceManager(serviceManager: TestServiceManager): ServiceManager
}