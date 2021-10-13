package ch.qscqlmpa.dwitch.service

import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ingame.services.ServiceManagerImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class AndroidServicesModule {

    @AppScope
    @Binds
    abstract fun bindServiceManager(serviceManager: ServiceManagerImpl): ServiceManager
}
