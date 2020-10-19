package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.persistence.Store
import io.mockk.clearMocks
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseUnitTest {

    protected val mockStore = mockk<Store>(relaxed = true)

    protected val mockInGameStore = mockk<InGameStore>(relaxed = true)

    protected val serializerFactory = SerializerFactory(Json.Default)

    open fun setup() {

    }

    open fun tearDown() {
        clearMocks(mockStore, mockInGameStore)
    }
}