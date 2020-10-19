package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import javax.inject.Inject

class GameRoomViewModel @Inject
constructor(disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory)
