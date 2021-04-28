package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.computerplayer.Computer
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class ResumeComputerPlayersUsecase @Inject constructor(
    private val store: InGameStore,
    private val computer: Computer
) {
    fun resumeComputerPlayers() {
        val info = store.getComputerPlayersToResume()
        Logger.debug { "Resume computer players (${info.playersId.size} players)" }
        info.playersId.forEach { id -> computer.resumeExistingPlayer(info.gameCommonId, id) }
    }
}