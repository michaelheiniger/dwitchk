package ch.qscqlmpa.dwitch.service

import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.ongoinggame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ongoinggame.services.HostInGameService
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManagerImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AndroidServiceBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeHostService(): HostInGameService

    @ContributesAndroidInjector
    abstract fun contributeGuestService(): GuestInGameService

    @AppScope
    @Binds
    abstract fun bindServiceManager(serviceManager: ServiceManagerImpl): ServiceManager
}
