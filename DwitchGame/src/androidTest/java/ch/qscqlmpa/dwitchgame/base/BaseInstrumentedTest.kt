package ch.qscqlmpa.dwitchgame.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.DaggerTestStoreComponent
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.util.*

/**
 * Note: The tests are executed on the AndroidJUnitRunner which has only one thread: subscribeOn()/observeOn() have no effect.
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseInstrumentedTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    protected val localPlayerName = "LocalGuest"
    protected val gameName = "Dwiiitch!!!"

    private lateinit var storeComponent: TestStoreComponent
    protected lateinit var inGameStore: InGameStore

    @Before
    fun setupStore() {
        storeComponent = DaggerTestStoreComponent.factory().create(context)
        val insertGameResult = storeComponent.store
            .insertGameForGuest(gameName, GameCommonId(UUID.randomUUID()), localPlayerName)
        val inGameStoreComponent = storeComponent.getInGameStoreComponentFactory()
            .create(insertGameResult.gameLocalId, insertGameResult.localPlayerLocalId)
        inGameStore = inGameStoreComponent.inGameStore
    }

    @After
    fun tearDownStore() {
        storeComponent.clearStore()
    }
}
