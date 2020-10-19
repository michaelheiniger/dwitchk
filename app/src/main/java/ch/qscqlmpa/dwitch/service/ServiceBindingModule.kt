package ch.qscqlmpa.dwitch.service

import ch.qscqlmpa.dwitch.ongoinggame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ongoinggame.services.HostInGameService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBindingModule {

    @OngoingGameScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun contributeHostService(): HostInGameService

    @OngoingGameScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun contributeGuestService(): GuestInGameService
}
