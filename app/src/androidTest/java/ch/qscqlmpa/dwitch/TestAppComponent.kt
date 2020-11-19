package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.app.AppComponent
import ch.qscqlmpa.dwitch.app.ApplicationModule
import ch.qscqlmpa.dwitch.gamediscovery.TestGameDiscoveryModule
import ch.qscqlmpa.dwitch.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitch.home.HomeModule
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameModule
import ch.qscqlmpa.dwitch.ongoinggame.TestOngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializationModule
import ch.qscqlmpa.dwitch.persistence.AppRoomDatabase
import ch.qscqlmpa.dwitch.persistence.TestDatabaseModule
import ch.qscqlmpa.dwitch.scheduler.SchedulerModule
import ch.qscqlmpa.dwitch.service.ServiceBindingModule
import ch.qscqlmpa.dwitch.service.ServiceManagerModule
import ch.qscqlmpa.dwitch.ui.home.HomeActivityBindingModule
import ch.qscqlmpa.dwitch.ui.home.HomeViewModelBindingModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ApplicationModule::class,
    SchedulerModule::class,
    SerializationModule::class,
    HomeActivityBindingModule::class,
    HomeModule::class,
    ServiceBindingModule::class,
    ServiceManagerModule::class,
    HomeViewModelBindingModule::class,
    TestDatabaseModule::class,
    TestGameDiscoveryModule::class
])
interface TestAppComponent : AppComponent {

    val database: AppRoomDatabase

    val testNetworkListener: TestNetworkAdapter

    override fun addInGameComponent(module: OngoingGameModule): TestOngoingGameComponent
}
