package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class TestServiceManagerModule {

    @Singleton
    @Binds
    abstract fun bindServiceManager(serviceManager: IntTestServiceManager): ServiceManager
}