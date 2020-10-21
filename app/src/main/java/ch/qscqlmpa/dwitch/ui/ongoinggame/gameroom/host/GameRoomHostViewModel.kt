package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import javax.inject.Inject

class GameRoomHostViewModel @Inject
constructor(disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory)