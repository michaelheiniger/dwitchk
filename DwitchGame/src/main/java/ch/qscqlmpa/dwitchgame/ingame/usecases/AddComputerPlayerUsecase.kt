package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.ingame.computerplayer.ComputerPlayersManager
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class AddComputerPlayerUsecase @Inject constructor(
    private val computerPlayersManager: ComputerPlayersManager
) {
    fun addPlayer(): Completable {
        return Completable.fromAction { computerPlayersManager.addNewPlayer() }
    }
}
