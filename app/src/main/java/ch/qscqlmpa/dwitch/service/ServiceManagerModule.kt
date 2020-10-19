package ch.qscqlmpa.dwitch.service

import ch.qscqlmpa.dwitch.ongoinggame.ServiceManager
import ch.qscqlmpa.dwitch.ongoinggame.ServiceManagerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ServiceManagerModule {

    @Singleton
    @Binds
    abstract fun bindServiceManager(serviceManager: ServiceManagerImpl): ServiceManager
}