package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.common.ApplicationConfig
import ch.qscqlmpa.dwitchcommunication.common.GameAdvertising

internal fun testApplicationConfig(): ApplicationConfig {
    return ApplicationConfig(
        gameAdvertising = GameAdvertising(port = 8888)
    )
}
