package ch.qscqlmpa.dwitchcommunication.common

import ch.qscqlmpa.dwitchcommunication.di.CommunicationScope
import com.sksamuel.hoplite.ConfigLoader
import javax.inject.Inject

@CommunicationScope
internal class ApplicationConfigRepository @Inject constructor() {
    val config: ApplicationConfig = ConfigLoader().loadConfigOrThrow("/communication-config.yaml")
}

internal data class GameAdvertising(val port: Int)
internal data class ApplicationConfig(val gameAdvertising: GameAdvertising)
