package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.common.SerializerFactory
import io.mockk.clearAllMocks
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal abstract class BaseUnitTest {

    protected val serializerFactory = SerializerFactory(Json)

    @AfterEach
    fun clearMocks() {
        clearAllMocks()
    }
}
