package ch.qscqlmpa.dwitchcommonutil.scheduler

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class TestSchedulerFactory constructor() : SchedulerFactory {

    private lateinit var timeScheduler: Scheduler

    constructor(timeScheduler: Scheduler) : this() {
        this.timeScheduler = timeScheduler
    }

    fun setTimeScheduler(timeScheduler: Scheduler) {
        this.timeScheduler = timeScheduler
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun single(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun timeScheduler(): Scheduler {
        return timeScheduler
    }
}
