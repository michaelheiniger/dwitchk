package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitch.ingame.services.TestServiceManagerModule
import ch.qscqlmpa.dwitch.persistence.IntTestDatabaseModule
import ch.qscqlmpa.dwitchgame.di.TestGameDiscoveryModule
import ch.qscqlmpa.dwitchgame.di.modules.HomeFacadeModule
import ch.qscqlmpa.dwitchgame.di.modules.TestSchedulerModule
import dagger.Component

@AppScope
@Component(
    modules = [
        TestSchedulerModule::class,
        TestGameDiscoveryModule::class,
        HomeFacadeModule::class,
        IntTestDatabaseModule::class,
        TestServiceManagerModule::class
    ]
)
interface IntTestAppComponent {

    @Component.Builder
    interface Builder {
        fun build(): IntTestAppComponent
    }

//    val database: AppRoomDatabase
//
//    val newGameUsecase: NewGameUsecase
//
//    val testNetworkListener: TestNetworkAdapter
//
//    val serviceManager: ServiceManager
//
//    fun addInGameComponent(module: OngoingGameModule): IntTestOngoingGameComponent
}
