package ch.qscqlmpa.dwitchgame.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameHostComponent
import ch.qscqlmpa.dwitchstore.DaggerTestStoreComponent
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreComponent
import ch.qscqlmpa.dwitchstore.store.TestStoreModule
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

/**
 * Note: The tests are executed on the AndroidJUnitRunner which has only one thread: subscribeOn()/observeOn() have no effect.
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseIntegrationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    protected val hostPlayerName = "Name of Host player"
    protected val gameName = "Dwiiitch!!!"
    protected val gameIpAddress = "192.168.1.1"
    protected val gamePort = 8889

    //    protected val storeComponent: TestStoreComponent = DaggerTestStoreComponent.factory().create(TestStoreModule(context))
    protected lateinit var storeComponent: TestStoreComponent
    protected lateinit var gameComponent: TestGameComponent
    protected var inGameStoreComponent: InGameStoreComponent? = null
    protected var communicationComponent: CommunicationComponent? = null
    protected var ongoingGameComponent: TestInGameHostComponent? = null

    protected lateinit var inGameStore: InGameStore

    @Before
    fun setupGameAndStoreComponents() {
        storeComponent = DaggerTestStoreComponent.factory().create(TestStoreModule(context))
//        gameComponent = DaggerTestGameComponent.factory().create(StoreModule(storeComponent.store))
    }

    @After
    fun tearDownStore() {
        storeComponent.clearStore()
    }
}
