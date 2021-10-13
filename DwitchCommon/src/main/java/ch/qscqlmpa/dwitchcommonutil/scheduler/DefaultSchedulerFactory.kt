package ch.qscqlmpa.dwitchcommonutil.scheduler

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class DefaultSchedulerFactory @Inject constructor() : SchedulerFactory {

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun single(): Scheduler {
        return Schedulers.single()
    }

    override fun timeScheduler(): Scheduler {
        return Schedulers.io()
    }
}
