package ch.qscqlmpa.dwitchgame.common

import ch.qscqlmpa.dwitchgame.di.GameScope
import com.sksamuel.hoplite.ConfigLoader
import javax.inject.Inject

@GameScope
internal class ApplicationConfigRepository @Inject constructor() {
    val config: ApplicationConfig = ConfigLoader().loadConfigOrThrow("/application.yaml")
}


data class GameAdvertising(val port: Int)
data class Communication(val waitForJoinOrRejoinAckTimeout: Long)
data class ApplicationConfig(val gameAdvertising: GameAdvertising, val communication: Communication)
