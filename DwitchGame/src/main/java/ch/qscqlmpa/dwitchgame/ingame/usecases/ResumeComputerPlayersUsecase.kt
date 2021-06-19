package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.ingame.computerplayer.ComputerPlayersManager
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class ResumeComputerPlayersUsecase @Inject constructor(
    private val store: InGameStore,
    private val computerPlayersManager: ComputerPlayersManager
) {
    fun resumeComputerPlayers() {
        val info = store.getComputerPlayersToResume()
        Logger.debug { "Resume computer players (${info.playersId.size} players)" }
        info.playersId.forEach { id -> computerPlayersManager.resumeExistingPlayer(info.gameCommonId, id) }
    }
}