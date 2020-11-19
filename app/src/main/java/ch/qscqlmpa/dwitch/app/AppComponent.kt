package ch.qscqlmpa.dwitch.app

import android.app.Application
import ch.qscqlmpa.dwitch.gamediscovery.GameDiscoveryModule
import ch.qscqlmpa.dwitch.home.HomeModule
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameModule
import ch.qscqlmpa.dwitch.persistence.DatabaseModule
import ch.qscqlmpa.dwitch.scheduler.SchedulerModule
import ch.qscqlmpa.dwitch.service.ServiceBindingModule
import ch.qscqlmpa.dwitch.service.ServiceManagerModule
import ch.qscqlmpa.dwitch.ui.home.HomeActivityBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ApplicationModule::class,
    DatabaseModule::class,
    HomeModule::class,
    HomeActivityBindingModule::class,
    HomeViewModelBindingModule::class,
    ServiceBindingModule::class,
    ServiceManagerModule::class,
    GameDiscoveryModule::class,
    SchedulerModule::class
])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun build(): AppComponent
    }

    fun addInGameComponent(module: OngoingGameModule): OngoingGameComponent
}
