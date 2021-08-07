package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal class GuestCommunicationModule {

    companion object {

        @OngoingGameScope
        @Provides
        fun provideGuestCommunicator(
            commClient: CommClient,
            guestCommunicationEventDispatcher: GuestCommunicationEventDispatcher,
            communicationStateRepository: GuestCommunicationStateRepository,
            schedulerFactory: SchedulerFactory
        ): GuestCommunicator {
            return GuestCommunicatorImpl(
                commClient,
                guestCommunicationEventDispatcher,
                communicationStateRepository,
                schedulerFactory
            )
        }
    }
}
