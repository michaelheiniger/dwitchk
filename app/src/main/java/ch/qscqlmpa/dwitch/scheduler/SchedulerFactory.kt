package ch.qscqlmpa.dwitch.scheduler

import io.reactivex.Scheduler

interface SchedulerFactory {

    fun ui(): Scheduler

    fun io(): Scheduler

    fun computation(): Scheduler

    fun timeScheduler(): Scheduler
}
