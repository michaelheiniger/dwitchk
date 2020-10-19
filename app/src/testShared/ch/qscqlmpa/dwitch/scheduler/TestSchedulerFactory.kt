package ch.qscqlmpa.dwitch.scheduler

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TestSchedulerFactory @Inject constructor() : SchedulerFactory {

    private lateinit var timeScheduler: Scheduler

    fun setTimeScheduler(timeScheduler: Scheduler) {
        this.timeScheduler = timeScheduler
    }

    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun timeScheduler(): Scheduler {
        return timeScheduler
    }
}
