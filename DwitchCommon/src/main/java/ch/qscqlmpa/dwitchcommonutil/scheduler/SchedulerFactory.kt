package ch.qscqlmpa.dwitchcommonutil.scheduler

import io.reactivex.rxjava3.core.Scheduler

interface SchedulerFactory {
    fun io(): Scheduler

    fun computation(): Scheduler

    fun timeScheduler(): Scheduler
}
