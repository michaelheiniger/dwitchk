package ch.qscqlmpa.dwitchgame.common

import ch.qscqlmpa.dwitchgame.di.GameScope
import com.sksamuel.hoplite.ConfigLoader
import javax.inject.Inject

@GameScope
internal class ApplicationConfigRepository @Inject constructor() {
    val config: ApplicationConfig = ConfigLoader().loadConfigOrThrow("/game-config.yaml")
}

internal data class Communication(val waitForJoinOrRejoinAckTimeout: Long)
internal data class ApplicationConfig(val communication: Communication)
