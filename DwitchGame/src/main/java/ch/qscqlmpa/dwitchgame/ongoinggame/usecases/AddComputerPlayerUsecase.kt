package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.computerplayer.Computer
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class AddComputerPlayerUsecase @Inject constructor(
    private val computer: Computer
) {
    fun addPlayer(): Completable {
        return Completable.fromAction { computer.addNewPlayer() }
    }
}