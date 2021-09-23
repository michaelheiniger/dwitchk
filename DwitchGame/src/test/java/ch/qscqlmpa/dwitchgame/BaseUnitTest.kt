package ch.qscqlmpa.dwitchgame

import ch.qscqlmpa.dwitchcommunication.common.SerializerFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.store.Store
import io.mockk.clearAllMocks
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseUnitTest {

    protected val mockStore = mockk<Store>(relaxed = true)

    protected val mockInGameStore = mockk<InGameStore>(relaxed = true)

    protected val serializerFactory = SerializerFactory(Json)

    @AfterEach
    fun clearMocks() {
        clearAllMocks()
    }
}
