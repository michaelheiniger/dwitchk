package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import javax.inject.Inject

class GameRoomViewModel @Inject
constructor(disposableManager: ch.qscqlmpa.dwitchcommonutil.DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory)
