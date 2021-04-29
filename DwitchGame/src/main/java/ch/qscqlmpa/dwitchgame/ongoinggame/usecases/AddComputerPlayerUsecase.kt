package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.computerplayer.ComputerPlayersManager
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class AddComputerPlayerUsecase @Inject constructor(
    private val computerPlayersManager: ComputerPlayersManager
) {
    fun addPlayer(): Completable {
        return Completable.fromAction { computerPlayersManager.addNewPlayer() }
    }
}