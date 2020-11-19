package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.gamediscovery.TestGameDiscoveryModule
import ch.qscqlmpa.dwitch.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitch.home.HomeModule
import ch.qscqlmpa.dwitch.ongoinggame.IntTestOngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameModule
import ch.qscqlmpa.dwitch.ongoinggame.TestServiceManagerModule
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitch.persistence.AppRoomDatabase
import ch.qscqlmpa.dwitch.persistence.IntTestDatabaseModule
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerModule
import ch.qscqlmpa.dwitch.usecases.NewGameUsecase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestSchedulerModule::class,
    TestGameDiscoveryModule::class,
    HomeModule::class,
    IntTestDatabaseModule::class,
    TestServiceManagerModule::class
])
interface IntTestAppComponent {

    @Component.Builder
    interface Builder {
        fun build(): IntTestAppComponent
    }

    val database: AppRoomDatabase

    val newGameUsecase: NewGameUsecase

    val testNetworkListener: TestNetworkAdapter

    val serviceManager: ServiceManager

    fun addInGameComponent(module: OngoingGameModule): IntTestOngoingGameComponent
}