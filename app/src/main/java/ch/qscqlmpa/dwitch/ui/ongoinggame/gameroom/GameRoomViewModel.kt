package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

class GameRoomViewModel @Inject constructor(
    private val uiScheduler: Scheduler
) : BaseViewModel()
