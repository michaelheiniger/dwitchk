package ch.qscqlmpa.dwitch.service

import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManagerImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ServiceManagerModule {

    @AppScope
    @Binds
    abstract fun bindServiceManager(serviceManager: ServiceManagerImpl): ServiceManager
}